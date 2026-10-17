<script setup>
    import '@/styles/character/appCharacterClass.css'
    import { ref, computed, onMounted } from 'vue'
    import CharacterStep from '@/vueComponents/shared/CharacterStep.vue'
    import CharacterPicker from '@/vueComponents/shared/CharacterPicker.vue'
    import { renderDescription } from '@/composables/renderDescription.js'

    const props = defineProps({ token: String, characterId: { type: [Number, String], required: true } })
    const emit = defineEmits(['navigate'])
    const API_BASE = 'http://localhost:8080/api'

    //Constantes del personaje
    const character = ref(null)
    const loading_character = ref(false)

    //Constantes de clases
    const classes_data = ref([])
    const loading_classes = ref(false)

    //Const clase seleccionada (para ver detalles)
    const viewed_class = ref(null)
    const viewed_loading = ref(false)
    const viewed_id = ref(null)

    //Features expandidas (por id)
    const expanded_features = ref(new Set())

    function toggleFeature(id) {
        if (expanded_features.value.has(id)) {
            expanded_features.value.delete(id)
        } else {
            expanded_features.value.add(id)
        }
    }

    //Const de tools
    const selected_tools_ids = ref([])
    const sourcesKey = `tool_sources_${props.characterId}`
    const tool_sources = ref(JSON.parse(localStorage.getItem(sourcesKey) ?? '{}'))

    function saveSource(name, added, granted) {
        tool_sources.value = { ...tool_sources.value, [name]: { added, granted } }
        localStorage.setItem(sourcesKey, JSON.stringify(tool_sources.value))
    }

    const class_tools = computed(() => viewed_class.value?.tools ?? [])
    const tool_limit = computed(() => viewed_class.value?.numberTools ?? 0)
    const tool_mode = computed(() => {
        const total = class_tools.value.length
        if (total === 0 || tool_limit.value === 0) return 'none'
        if (total <= tool_limit.value) return 'auto'
        return 'choose'
    })
    const pregranted_tools_ids = computed(() => {
        const classAdded = tool_sources.value.class?.added ?? []
        return (character.value?.tools ?? [])
            .map(t => t.id)
            .filter(id => !classAdded.includes(id))
    })
    const pregranted_in_class = computed(() =>
        class_tools.value.filter(t => pregranted_tools_ids.value.includes(t.id)).length
    )
    const picks_left = computed(() =>
        tool_limit.value - pregranted_in_class.value - selected_tools_ids.value.length
    )


    //Guardado
    const saving = ref(false)
    const error = ref('')

    //Metodos
    async function fetchCharacter() {
        loading_character.value = true
        try {
            const res = await fetch(`${API_BASE}/characters/${props.characterId}`, {
                headers: { Authorization: `Bearer ${props.token}` }
            })
            character.value = await res.json()
        } catch (e) {
            console.error(e)
        } finally {
            loading_character.value = false
        }
    }

    async function fetchClasses() {
        loading_classes.value = true
        try{        
            const res = await fetch(`${API_BASE}/classes`, {
            headers: { Authorization: `Bearer ${props.token}` }
            })
            const data = await res.json()
            classes_data.value = data.content

        }catch (e){
            console.error(e)
        }finally{
            loading_classes.value = false
        }
    }

    onMounted(async () => {
        await fetchCharacter()
        fetchClasses()
        // Si ya tiene clase, lo cargamos como el que se está viendo
        if (character.value?.classes?.[0]?.classEntity?.id) {
            await pickClassToView(character.value.classes[0].classEntity)
        }
    })

    async function pickClassToView(class_data) {
        viewed_loading.value = true
        try{
            const res = await fetch(`${API_BASE}/classes/${class_data.id}`, {
                headers: { Authorization: `Bearer ${props.token}` }
            })
            viewed_class.value = await res.json()
            viewed_id.value = class_data.id
            selected_tools_ids.value = []
            expanded_features.value = new Set()
        }catch(e){
            console.error(e)
        }finally{
            viewed_loading.value = false
        }
    }

    async function selectClass(classId) {
        saving.value = true
        error.value = ''
        try {
            //Como en la creación solo puede haber una clase, si ya tiene una
            //distinta a la elegida, primero la quitamos
            const currentClass = character.value?.classes?.[0]
            if (currentClass && currentClass.classEntity.id !== classId) {
                const deleteRes = await fetch(`${API_BASE}/characters/${props.characterId}/classes/${currentClass.classEntity.id}`, {
                    method: 'DELETE',
                    headers: { Authorization: `Bearer ${props.token}` }
                })
                if (!deleteRes.ok) {
                    error.value = 'Error removing previous class'
                    return
                }
                if (!(await removePreviousClassTools())) {
                    error.value = 'Error removing previous class tools'
                    return
                }
                await fetchCharacter()
            }

            const res = await fetch(`${API_BASE}/characters/${props.characterId}/classes/${classId}`, {
                method: 'POST',
                headers: {
                    Authorization: `Bearer ${props.token}`
                }
            })
            if (!res.ok) {
                error.value = 'Error al guardar la clase'
                return
            }
            //Patch temporal para llenar skills por el momento
            await initializeClassSkills()
            await insertClassTools()
            await fetchCharacter()
            selected_tools_ids.value = []
        } catch (e) {
            console.error(e)
            error.value = 'Error de conexión'
        } finally {
            saving.value = false
        }
    }

    //Metodos de la gestion de tools
    function isPregranted(toolId) {
        return pregranted_tools_ids.value.includes(toolId)
    }

    function isToolSelected(toolId) {
        return isPregranted(toolId) || selected_tools_ids.value.includes(toolId)
    }

    function canToggleOn() {
        return picks_left.value > 0
    }

    function toggleTool(toolId) {
        if (isPregranted(toolId)) return
        if (selected_tools_ids.value.includes(toolId)) {
            selected_tools_ids.value = selected_tools_ids.value.filter(id => id !== toolId)
        } else if (canToggleOn()) {
            selected_tools_ids.value = [...selected_tools_ids.value, toolId]
        }
    }

    // Devuelve todos los ids de tools que da la clase según el modo
    function getToolsGranted() {
        if (tool_mode.value === 'auto') return class_tools.value.map(t => t.id)
        if (tool_mode.value === 'choose') return [...selected_tools_ids.value]
        return []
    }

    async function insertClassTools() {
        const granted = getToolsGranted()
        const owned = new Set(pregranted_tools_ids.value)
        const toAdd = granted.filter(id => !owned.has(id))
        let added = []

        if (toAdd.length) {
            try {
                const results = await Promise.all(
                    toAdd.map(id =>
                        fetch(`${API_BASE}/characters/${props.characterId}/tools/${id}`, {
                            method: 'POST',
                            headers: { Authorization: `Bearer ${props.token}` }
                        })
                    )
                )
                added = toAdd.filter((id, i) => results[i].ok)
                if (results.some(r => !r.ok)) error.value = 'Error saving some tools.'
            } catch (e) {
                console.error(e)
                error.value = 'Connection error.'
            }
        }
        saveSource('class', added, granted)
    }

    async function removePreviousClassTools() {
        const src = tool_sources.value
        const others = new Set(
            Object.entries(src)
                .filter(([name]) => name !== 'class')
                .flatMap(([, v]) => v.granted)
        )
        const ids = (src.class?.added ?? []).filter(id => !others.has(id))

        if (ids.length) {
            const results = await Promise.all(
                ids.map(id =>
                    fetch(`${API_BASE}/characters/${props.characterId}/tools/${id}`, {
                        method: 'DELETE',
                        headers: { Authorization: `Bearer ${props.token}` }
                    })
                )
            )
            if (results.some(r => !r.ok)) return false
        }
        saveSource('class', [], [])
        return true
    }

    async function initializeClassSkills() {
        let fullSkillList = []
        try {
            const res = await fetch(`${API_BASE}/skills`, {
                headers: { Authorization: `Bearer ${props.token}` }
            })
            const skillList = await res.json()
            fullSkillList = skillList.content ?? []
        } catch (e) {
            console.error('Error obteniendo el listado completo de skills', e)
            return
        }

        const existing = (character.value?.skills ?? []).map(s => ({
            skillId: s.skill.id,
            proficient: s.proficient,
            expertise: s.expertise
        }))

        const merged = [...existing]
        fullSkillList.forEach(s => {
            if (!merged.find(m => m.skillId === s.id)) {
                merged.push({ skillId: s.id, proficient: false, expertise: false })
            }
        })

        try {
            await fetch(`${API_BASE}/characters/${props.characterId}/skills`, {
                method: 'PATCH',
                headers: {
                    Authorization: `Bearer ${props.token}`,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(merged)
            })
        } catch (e) {
            console.error(e)
        }
    }

    function featuresByLevel() {
        if (!viewed_class.value?.features) return []
        const levels = [...new Set(viewed_class.value.features.map(f => f.level))].sort((a, b) => a - b)
        return levels.map(level => ({
            level,
            features:viewed_class.value.features.filter(f => f.level === level)
        }))
    }

    function goBack() {
        emit('navigate', 'characters')
    }
</script>

<template>
    <CharacterStep
        back-label="Back to Characters"
        :error="error"
        :continue-disabled="!character?.classes?.length"
        @back="goBack"
        @continue="emit('navigate', { page: 'characterSpecies', characterId: props.characterId })"
    >
        <!--Cabecera con info del personaje-->
        <template #header>
            <div v-if="loading_character">Loading Character...</div>
            <div v-else-if="character" class="character_header">
                <h1>{{ character.name }}</h1>
                <span v-if="character.classes?.length" class="header_subtitle">
                    Current Class: {{ character.classes[0].classEntity.name }}
                </span>
                <span v-else class="character_class_current--empty">
                    No Class assigned
                </span>
            </div>
        </template>

        <!--Zona fija: selector de clase y botón de elegir-->
        <template #toolbar>
            <CharacterPicker
                noun="Class"
                :options="classes_data"
                :loading="loading_classes"
                :viewed-id="viewed_id"
                :viewed-name="viewed_class?.name"
                :current-id="character?.classes?.[0]?.classEntity?.id"
                :saving="saving"
                :pick-disabled="tool_mode === 'choose' && picks_left !== 0"
                :hint="tool_mode === 'choose' && picks_left !== 0 ? `Choose ${picks_left} more tool proficiency below` : ''"
                @view="pickClassToView"
                @pick="selectClass(viewed_id)"
            />
        </template>

        <!--Detalles de la clase elegida en el desplegable-->
        <div v-if="viewed_loading" class="loading">Loading...</div>
        <div v-else-if="viewed_class" class="class_details_card">
            <div v-no-double-select class="class_details_header">
                <span class="class_details_name">{{ viewed_class.name }}</span>
                <span class="class_details_hitpoint">Dice: {{ viewed_class.hitPointDie }}</span>
            </div>

            <div v-if="viewed_class.savingThrows?.length" class="comp_class_saving_throws">
                <span class="comp_class_saving_throws_label">Saving Throw Proficiencies:</span>
                <span v-for="p in viewed_class.savingThrows" :key="p.ability" class="comp_class_saving_throws_chip">
                    {{ p.ability }}
                </span>
            </div>
            <div v-if="viewed_class.armorTypes?.length" class="class_armor_types">
                <span class="class_section_label">Armor Training</span>
                <span v-for="a in viewed_class.armorTypes" :key="a.armorType" class="class_armor_chip">
                    {{ a.armorType}}
                </span> 
            </div>
            <div v-else class="class_armor_types">
                <span class="class_section_label">Armor Training</span>
                <span class="class_armor_chip">
                    None
                </span> 
            </div>

            <div v-if="viewed_class.skills?.length" class="comp_class_skills">
                <h3>Skill Proficiencies ({{viewed_class.numberSkills}} to pick)</h3>
                <div v-for="skill in viewed_class.skills" :key="skill.skill" class="view_skill_row">
                    <span>{{ skill.skill }}</span>
                    <span>({{ skill.ability }})</span>
                </div>
            </div>

            <div v-if="viewed_class.features?.length" class="class_features_section">
                <h3>Features by level:</h3>
                <div v-for="group in featuresByLevel()" :key="group.level" class="class_feature_level_group">
                    <h4 class="class_feature_level_title">Level {{ group.level }}</h4>
                    <div v-for="f in group.features" :key="f.feature.id" class="class_feature_row">
                        <div class="class_feature_header" @click="toggleFeature(f.feature.id)">
                            <span class="class_feature_name">{{ f.feature.name }}</span>
                            <button class="class_feature_toggle_btn" :class="{ 'class_feature_toggle_btn--open': expanded_features.has(f.feature.id) }">
                                {{ expanded_features.has(f.feature.id) ? '−' : '+' }}
                            </button>
                        </div>
                        <div v-if="expanded_features.has(f.feature.id)" class="class_feature_description" v-html="renderDescription(f.feature.description)"></div>
                    </div>
                </div>
            </div>

            <!-- Tools automáticas -->
            <div v-if="tool_mode === 'auto'" class="class_tools_auto">
                <h3>Tool proficiencies (granted)</h3>
                <span v-for="tool in class_tools" :key="tool.id" class="class_armor_chip">
                    {{ tool.name }}
                </span>
            </div>
            <!-- Tools a elegir -->
            <div v-if="tool_mode === 'choose'" class="class_tools_choose">
                <h3>
                    Tool proficiencies
                    <span>({{ tool_limit - picks_left }} / {{ tool_limit }})</span>
                </h3>
                <div class="tool_list">
                    <label v-for="tool in class_tools" :key="tool.id" class="tool_row">
                        <input type="checkbox" :checked="isToolSelected(tool.id)" :disabled="isPregranted(tool.id) || (!isToolSelected(tool.id) && !canToggleOn())" @change="toggleTool(tool.id)"/>
                        {{ tool.name }}
                        <span v-if="isPregranted(tool.id)" class="tool_granted">(already proficient)</span>
                    </label>
                </div>
            </div>
        </div>
        <p v-else-if="!loading_character" class="empty_hint">Select a class from the list to see its details.</p>
    </CharacterStep>
</template>

<style scoped>
    /* ---------- Cabecera ---------- */

    .character_class_current--empty {
        color: var(--text-secondary);
        font-size: 0.9rem;
    }

    /* ---------- Tarjeta de detalle ---------- */
    .class_details_card {
        display: flex;
        flex-direction: column;
        gap: 14px;
        width: 100%;
        box-sizing: border-box;
        padding: 16px;
        border: 1px solid var(--border);
        background: var(--surface);
    }

    .class_details_header {
        display: flex;
        align-items: baseline;
        justify-content: space-between;
        gap: 12px;
        padding-bottom: 10px;
        border-bottom: 1px solid var(--separator-lines);
    }

    .class_details_name {
        font-size: 1.3rem;
        font-weight: 700;
    }

    .class_details_hitpoint {
        color: var(--text-secondary);
    }

    .comp_class_saving_throws,
    .class_armor_types,
    .class_tools_auto {
        display: flex;
        align-items: center;
        flex-wrap: wrap;
        gap: 6px;
    }

    .comp_class_saving_throws_label,
    .class_section_label {
        margin-right: 6px;
        font-weight: bold;
    }

    .comp_class_saving_throws_chip,
    .class_armor_chip {
        padding: 2px 10px;
        border: 1px solid var(--border);
        border-radius: 999px;
        font-size: 0.85rem;
        text-transform: capitalize;
    }

    .comp_class_skills h3,
    .class_features_section h3,
    .class_tools_auto h3,
    .class_tools_choose h3 {
        margin: 0 0 6px;
        font-size: 1.05rem;
    }

    .class_tools_auto h3 {
        width: 100%;
    }

    /* Habilidades */
    .comp_class_skills {
        display: grid;
        grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
        gap: 4px 16px;
    }

    .comp_class_skills h3 {
        grid-column: 1 / -1;
    }

    .view_skill_row {
        display: flex;
        gap: 6px;
    }

    .view_skill_row span:last-child {
        color: var(--text-secondary);
    }

    /* Rasgos por nivel */
    .class_feature_level_group {
        margin-bottom: 14px;
    }

    .class_feature_level_title {
        margin: 0 0 6px;
        padding-bottom: 4px;
        border-bottom: 1px solid var(--separator-lines);
        font-size: 1rem;
    }

    .class_feature_row {
        margin-bottom: 6px;
        border: 1px solid var(--separator-lines);
        border-radius: 6px;
        overflow: hidden;
    }

    .class_feature_header {
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding: 8px 12px;
        cursor: pointer;
        user-select: none;
    }

    .class_feature_header:hover {
        background: var(--surface-hover-strong);
    }

    .class_feature_name {
        font-weight: bold;
    }

    .class_feature_toggle_btn {
        width: 24px;
        height: 24px;
        border: 1px solid var(--border);
        border-radius: 50%;
        background: var(--surface);
        font-weight: bold;
        line-height: 1;
        cursor: pointer;
    }

    .class_feature_toggle_btn--open {
        border-color: var(--accent-strong-border);
        background: var(--accent-strong);
        color: var(--text-on-accent);
    }

    .class_feature_description {
        padding: 10px 12px;
        border-top: 1px solid var(--separator-lines);
        font-size: 15px;
        line-height: 1.5;
    }

    /* El contenido de v-html no lleva el atributo de scoped: hay que usar :deep */
    .class_feature_description :deep(table) {
        width: 100%;
        margin: 8px 0;
        border-collapse: collapse;
        font-size: 14px;
    }

    .class_feature_description :deep(th),
    .class_feature_description :deep(td) {
        padding: 4px 10px;
        border: 1px solid var(--separator-lines);
        text-align: left;
        vertical-align: top;
    }

    /* Herramientas a elegir */
    .tool_list {
        display: grid;
        grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
        gap: 6px;
    }

    .tool_row {
        display: flex;
        align-items: center;
        gap: 8px;
        padding: 4px 8px;
        border-radius: 6px;
        cursor: pointer;
    }

    .tool_row:hover {
        background: var(--surface-hover-strong);
    }

    .tool_granted {
        margin-left: auto;
        font-size: 0.75rem;
        color: var(--text-secondary);
    }
</style>