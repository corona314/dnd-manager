<script setup>
    import '@/styles/character/appCharacterBackground.css'
    import CharacterStep from '@/vueComponents/shared/CharacterStep.vue'
    import { ref, onMounted } from 'vue'
    import CharacterBackButton from '../../shared/CharacterBackButton.vue'

    const props = defineProps({ token: String, characterId: { type: [Number, String], required: true } })
    const emit = defineEmits(['navigate'])
    const API_BASE = 'http://localhost:8080/api'

    //Constantes del personaje
    const character = ref(null)
    const loading_character = ref(false)

    //Constantes de trasfondos
    const backgrounds = ref([])
    const loading_background = ref(false)
    const background_open = ref(false)

    //Const trasfondo seleccionado (para ver detalles)
    const viewed_background = ref(null)
    const viewed_loading = ref(false)
    const viewed_id = ref(null)

    //Guardado
    const saving = ref(false)
    const error = ref('')

    //Registro de lo que ha dado cada fuente (clase, trasfondo...), para poder quitarlo luego
    //sin tocar lo que viene de otra fuente. Las herramientas comparten registro con AppCharacterClass.
    const toolSourcesKey = `tool_sources_${props.characterId}`
    const skillSourcesKey = `skill_sources_${props.characterId}`

    function readSources(key) {
        try {
            return JSON.parse(localStorage.getItem(key) ?? '{}')
        } catch {
            return {}
        }
    }

    function saveSource(key, name, added, granted) {
        const sources = readSources(key)
        sources[name] = { added, granted }
        localStorage.setItem(key, JSON.stringify(sources))
    }

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

    async function fetchBackgrounds() {
        loading_background.value = true
        try{        
            const res = await fetch(`${API_BASE}/backgrounds`, {
            headers: { Authorization: `Bearer ${props.token}` }
            })
            const data = await res.json()
            backgrounds.value = data.content

        }catch (e){
            console.error(e)
        }finally{
            loading_background.value = false
        }
    }

    onMounted(async () => {
        fetchBackgrounds()
        await fetchCharacter()
        // Si ya tiene trasfondo, lo cargamos como el que se está viendo
        if (character.value?.background?.id) {
            await pickBackgroundToView(character.value.background)
        }
    })

    async function pickBackgroundToView(background) {
        background_open.value = false
        viewed_loading.value = true
        try{
            const res = await fetch(`${API_BASE}/backgrounds/${background.id}`, {
                headers: { Authorization: `Bearer ${props.token}` }
            })
            viewed_background.value = await res.json()
            viewed_id.value = background.id
        }catch(e){
            console.error(e)
        }finally{
            viewed_loading.value = false
        }
    }

    async function selectBackground(backgroundId) {
        saving.value = true
        error.value = ''
        try {
            //Si ya tenía otro trasfondo, primero quitamos lo que dio ese trasfondo
            const previous = character.value?.background
            if (previous && previous.id !== backgroundId) {
                if (!(await removePreviousBackgroundTools())) {
                    error.value = 'Error while removing previous background tools'
                    return
                }
                if (!(await removePreviousBackgroundSkills())) {
                    error.value = 'Error while removing previous background skills'
                    return
                }
                await fetchCharacter()
            }

            const res = await fetch(`${API_BASE}/characters/${props.characterId}`, {
                method: 'PATCH',
                headers: {
                    Authorization: `Bearer ${props.token}`,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ backgroundId: backgroundId })
            })
            if (!res.ok) {
                error.value = 'Error saving background'
                return
            }
            await applyBackgroundSkills()
            await applyBackgroundTools()
            await fetchCharacter()
        } catch (e) {
            console.error(e)
            error.value = 'Connection error'
        } finally {
            saving.value = false
        }
    }

    //Quita las herramientas que añadió el trasfondo anterior (salvo las que dé otra fuente)
    async function removePreviousBackgroundTools() {
        const sources = readSources(toolSourcesKey)
        const others = new Set(
            Object.entries(sources)
                .filter(([name]) => name !== 'background')
                .flatMap(([, v]) => v.granted ?? [])
        )
        const ids = (sources.background?.added ?? []).filter(id => !others.has(id))

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
        saveSource(toolSourcesKey, 'background', [], [])
        return true
    }

    //Quita las competencias que activó el trasfondo anterior (solo las que él activó)
    async function removePreviousBackgroundSkills() {
        const sources = readSources(skillSourcesKey)
        const ids = new Set(sources.background?.added ?? [])

        if (ids.size) {
            const merged = (character.value?.skills ?? []).map(s => ({
                skillId: s.skill.id,
                proficient: ids.has(s.skill.id) ? false : s.proficient,
                expertise: ids.has(s.skill.id) ? false : s.expertise
            }))

            const res = await fetch(`${API_BASE}/characters/${props.characterId}/skills`, {
                method: 'PATCH',
                headers: {
                    Authorization: `Bearer ${props.token}`,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(merged)
            })
            if (!res.ok) return false
        }
        saveSource(skillSourcesKey, 'background', [], [])
        return true
    }

    async function applyBackgroundSkills() {
        const grantedSkills = viewed_background.value?.skills ?? []
        if (!grantedSkills.length) {
            saveSource(skillSourcesKey, 'background', [], [])
            return
        }

        const existing = (character.value?.skills ?? []).map(s => ({
            skillId: s.skill.id,
            proficient: s.proficient,
            expertise: s.expertise
        }))

        //'added' = solo las competencias que este trasfondo ha activado de verdad
        //(las que ya eran competentes no cuentan, para no quitarlas después)
        const merged = [...existing]
        const added = []
        grantedSkills.forEach(gs => {
            const found = merged.find(m => m.skillId === gs.id)
            if (found) {
                if (!found.proficient) {
                    found.proficient = true
                    added.push(gs.id)
                }
            } else {
                merged.push({ skillId: gs.id, proficient: true, expertise: false })
                added.push(gs.id)
            }
        })

        try {
            const res = await fetch(`${API_BASE}/characters/${props.characterId}/skills`, {
                method: 'PATCH',
                headers: {
                    Authorization: `Bearer ${props.token}`,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(merged)
            })
            if (!res.ok) {
                error.value = 'Error saving background skills'
                return
            }
            saveSource(skillSourcesKey, 'background', added, grantedSkills.map(g => g.id))
        } catch (e) {
            console.error(e)
            error.value = 'Connection error while saving skills'
        }
    }

    async function applyBackgroundTools() {
        const granted = (viewed_background.value?.tools ?? []).map(t => t.id)
        const existingIds = new Set((character.value?.tools ?? []).map(t => t.id))
        const toAdd = granted.filter(id => !existingIds.has(id))
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
                if (results.some(r => !r.ok)) {
                    error.value = 'Error saving background tools'
                }
            } catch (e) {
                console.error(e)
                error.value = 'Connection error while saving tools'
            }
        }
        saveSource(toolSourcesKey, 'background', added, granted)
    }

    function goBack() {
        emit('navigate', 'characterSpecies')
    }
</script>

<template>
    <CharacterStep
        :error="error"
        :continue-disabled="!character?.background"
        @back="goBack"
        @continue="emit('navigate', { page: 'characterAbilities', characterId: props.characterId })"
    >
        <!--Cabecera con info del personaje-->
        <template #header>
            <div v-if="loading_character">Loading Character...</div>
            <div v-else-if="character" class="character_background_header">
                <h1>{{ character.name }}</h1>
                <span v-if="character.background" class="header_subtitle">
                    Current Background: {{ character.background.name }}
                </span>
                <span v-else class="character_background_current character_background_current--empty">
                    No Background assigned
                </span>
            </div>
        </template>

        <!--Desplegable de trasfondos-->
        <h2>Pick a Background</h2>
        <div class="background_filter">
            <div class="background_dropdown_btn" @click="background_open = !background_open">
                {{ viewed_background ? viewed_background.name : 'Select a Background' }} <i>▾</i>
            </div>
            <div v-if="background_open" class="background_dropdown_menu">
                <div v-if="loading_background" class="loading">Loading...</div>
                <div v-else v-for="b in backgrounds" :key="b.id" class="background_option" :class="{ selected: character?.background?.id === b.id }" @click="pickBackgroundToView(b)">
                    {{ b.name }}
                </div>
            </div>
        </div>

        <!--Detalles del trasfondo elegido en el desplegable-->
        <div v-if="viewed_loading" class="loading">Loading...</div>
        <div v-else-if="viewed_background" class="background_details_card">
            <div class="background_details_header">
                <span class="background_details_name">{{ viewed_background.name }}</span>
                <div></div>
                <span class="background_details_description">{{ viewed_background.description }}</span>
            </div>
            <div v-if="viewed_background.skills?.length" class="background_skills">
                <span class="background_section_label">Skills:</span>
                <span v-for="s in viewed_background.skills" :key="s.skill" class="background_skill_chip">
                    {{ s.skill }} ({{ s.ability }})
                </span>
            </div>
            <div v-if="viewed_background.tools?.length" class="background_tools">
                <span class="background_section_label">Tools (just 1):</span>
                <span v-for="t in viewed_background.tools" :key="t.id" class="background_tool_chip" :title="`${t.weight} lb · ${t.rarity}`">
                    {{ t.name }}
                </span>
            </div>

            <button class="general_button background_details_select_btn" @click="selectBackground(viewed_id)" :disabled="saving || character?.background?.id === viewed_id">
                {{ saving ? 'Saving...' : (character?.background?.id === viewed_id ? 'Current Background' : `Pick ${viewed_background.name}`) }}
            </button>
        </div>
    </CharacterStep>
</template>

<style scoped>
    /* Dentro de CharacterStep los hijos ocupan todo el ancho de la columna */
    h2 {
        text-align: center;
    }

    .background_details_card {
        width: 100%;
        box-sizing: border-box;
    }

    /* Cabecera en una sola fila y compacta */
    .character_background_header {
        display: flex;
        align-items: center;
        justify-content: center;
        flex-wrap: wrap;
        gap: 6px 16px;
    }

    .character_background_header h1 {
        margin: 0;
        font-size: 1.5rem;
    }

    /* El menú del desplegable flota encima del contenido en vez de empujarlo */
    .background_filter {
        position: relative;
        align-self: center;
    }

    .background_dropdown_menu {
        position: absolute;
        top: 100%;
        left: 0;
        z-index: 10;
        min-width: 100%;
        max-height: 260px;
        overflow-y: auto;
        padding: 5px;
        border: 1px solid var(--border);
        border-radius: 4px;
        background: var(--default);
    }
</style>
