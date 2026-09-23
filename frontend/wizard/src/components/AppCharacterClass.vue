<script setup>
    import './styles/appCharacterClass.css'
    import { ref, computed, onMounted } from 'vue'
    const props = defineProps({ token: String, characterId: { type: [Number, String], required: true } })
    const emit = defineEmits(['navigate'])
    const API_BASE = 'http://localhost:8080/api'

    //Constantes del personaje
    const character = ref(null)
    const loading_character = ref(false)

    //Constantes de clases
    const classes_data = ref([])
    const loading_classes = ref(false)
    const class_open = ref(false)

    //Const clase seleccionada (para ver detalles)
    const viewed_class = ref(null)
    const viewed_loading = ref(false)
    const viewed_id = ref(null)

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

    onMounted(() => {
        fetchCharacter()
        fetchClasses()
    })

    async function pickClassToView(class_data) {
        class_open.value = false
        viewed_loading.value = true
        try{
            const res = await fetch(`${API_BASE}/classes/${class_data.id}`, {
                headers: { Authorization: `Bearer ${props.token}` }
            })
            viewed_class.value = await res.json()
            viewed_id.value = class_data.id
            selected_tools_ids.value = []
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
                    error.value = 'Error al quitar la clase anterior'
                    return
                }
                if (!(await removePreviousClassTools())) {
                    error.value = 'Error al quitar las herramientas anteriores'
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
                if (results.some(r => !r.ok)) error.value = 'Error al guardar las herramientas de clase'
            } catch (e) {
                console.error(e)
                error.value = 'Error de conexión al guardar herramientas'
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

    function goBackToCharacters() {
        emit('navigate', 'characters')
    }
</script>

<template>
    <div class="character_class_page">
        <!--Cabecera con info del personaje-->
        <div v-if="loading_character">Cargando personaje...</div>
        <div v-else-if="character" class="character_class_header">
            <h1>{{ character.name }}</h1>
            <span v-if="character.classes?.length" class="character_class_current">
                Current Class: {{ character.classes[0].classEntity.name }}
            </span>
            <span v-else class="character_class_current character_class_current--empty">
                No Class assigned
            </span>
        </div>

        <span v-if="error" class="character_class_error">{{ error }}</span>

        <!--Desplegable de clases-->
        <h2>Pick a Class</h2>
        <div class="class_filter">
            <div class="class_dropdown_btn" @click="class_open = !class_open">
                {{ viewed_class ? viewed_class.name : 'Selecciona una clase' }} <i>▾</i>
            </div>
            <div v-if="class_open" class="class_dropdown_menu">
                <div v-if="loading_classes" class="loading">Cargando...</div>
                <div v-else v-for="class_data in classes_data" :key="class_data.name" class="class_option" :class="{ selected: character?.classes?.[0]?.classEntity?.id === class_data.id }" @click="pickClassToView(class_data)">
                    {{ class_data.name }}
                </div>
            </div>
        </div>

        <!--Detalles de la clase elegida en el desplegable-->
        <div v-if="viewed_loading" class="loading">Cargando...</div>
        <div v-else-if="viewed_class" class="class_details_card">
            <div class="class_details_header">
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
                <h3>Skill Proficiencies({{viewed_class.numberSkills}} to pick)</h3>
                <div v-for="skill in viewed_class.skills" :key="skill.skill.id" class="view_skill_row">
                    <span>{{ skill.skill }}</span>
                    <span>({{ skill.ability }})</span>
                </div>
            </div>

            <div v-if="viewed_class.features?.length" class="class_features_section">
                <h3>Feats by level:</h3>
                <div v-for="group in featuresByLevel()" :key="group.level" class="class_feature_level_group">
                    <h4 class="class_feature_level_title">Nivel {{ group.level }}</h4>
                    <div v-for="f in group.features" :key="f.feature.id" class="class_feature_row">
                        <div class="class_feature_header" @click="toggleFeature(f.feature.id)">
                            <span class="class_feature_name">{{ f.feature.name }}</span>
                        </div>
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
            <div v-if="tool_mode === 'choose'" class="finalize_class_skills">
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

            <button class="class_details_select_btn" @click="selectClass(viewed_id)" :disabled="saving || character?.classes?.[0]?.classEntity?.id === viewed_id || (tool_mode === 'choose' && picks_left !== 0)">
                {{ saving ? 'Guardando...' : (character?.classes?.[0]?.classEntity?.id === viewed_id ? 'Clase actual' : `Elegir ${viewed_class.name}`) }}
            </button>
        </div>
        <button class="character_class_forward" @click="emit('navigate', {page: 'characterSpecie', characterId: props.characterId})" :disabled="!character?.classes?.length">Continue Creation</button>
        <button class="character_class_back" @click="goBackToCharacters">Volver a mis personajes</button>
    </div>
</template>

<style>

</style>