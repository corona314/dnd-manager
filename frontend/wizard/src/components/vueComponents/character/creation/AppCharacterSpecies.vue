<script setup>
    import '@/styles/character/appCharacterSpecies.css'
    import { ref, onMounted } from 'vue'
    import CharacterStep from '@/vueComponents/shared/CharacterStep.vue'
    import { renderDescription } from '@/composables/renderDescription.js'
    const props = defineProps({ token: String, characterId: { type: [Number, String], required: true } })
    const emit = defineEmits(['navigate'])
    const API_BASE = 'http://localhost:8080/api'

    //Constantes del personaje
    const character = ref(null)
    const loading_character = ref(false)

    //Constantes de Especie
    const species = ref([])
    const loading_species = ref(false)
    const specie_open = ref(false)

    //Const clase seleccionada (para ver detalles)
    const viewed_specie = ref(null)
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

    async function fetchSpecies() {
        loading_species.value = true
        try{        
            const res = await fetch(`${API_BASE}/species`, {
            headers: { Authorization: `Bearer ${props.token}` }
            })
            const data = await res.json()
            species.value = data.content

        }catch (e){
            console.error(e)
        }finally{
            loading_species.value = false
        }
    }

    onMounted(async () => {
        fetchSpecies()
        await fetchCharacter()
        // Si ya tiene especie, lo cargamos como el que se está viendo
        if (character.value?.species?.id) {
            await pickSpecieToView(character.value.species)
        }
    })

    async function pickSpecieToView(specie) {
        specie_open.value = false
        viewed_loading.value = true
        try{
            const res = await fetch(`${API_BASE}/species/${specie.id}`, {
                headers: { Authorization: `Bearer ${props.token}` }
            })
            viewed_specie.value = await res.json()
            viewed_id.value = specie.id
            expanded_features.value = new Set()
        }catch(e){
            console.error(e)
        }finally{
            viewed_loading.value = false
        }
    }

    async function selectSpecie(specieId) {
        saving.value = true
        error.value = ''
        try {
            const res = await fetch(`${API_BASE}/characters/${props.characterId}`, {
                method: 'PATCH',
                headers: {
                    Authorization: `Bearer ${props.token}`,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ speciesId: specieId, walkSpeed: viewed_specie.value?.walkSpeed ?? null, flySpeed: viewed_specie.value?.flySpeed ?? null, })
            })
            if (!res.ok) {
                error.value = 'Error saving species'
                return
            }
            await fetchCharacter()
        } catch (e) {
            console.error(e)
            error.value = 'Connection error'
        } finally {
            saving.value = false
        }
    }

    function goBack() {
        emit('navigate', 'characterClass')
    }
</script>

<template>
    <CharacterStep
        :error="error"
        :continue-disabled="!character?.species"
        @back="goBack"
        @continue="emit('navigate', { page: 'characterBackground', characterId: props.characterId })"
    >
        <!--Cabecera con info del personaje-->
        <template #header>
            <div v-if="loading_character">Loading Character...</div>
            <div v-else-if="character" class="character_specie_header">
                <h1>{{ character.name }}</h1>
                <span v-if="character.species" class="header_subtitle">
                    Current Species: {{ character.species.name }}
                </span>
                <span v-else class="character_specie_current character_specie_current--empty">
                    No Species assigned
                </span>
            </div>
        </template>

        <!--Desplegable de especies-->
        <h2>Pick a Species</h2>
        <div class="specie_filter">
            <div class="specie_dropdown_btn" @click="specie_open = !specie_open">
                {{ viewed_specie ? viewed_specie.name : 'Select a Species' }} <i>▾</i>
            </div>
            <div v-if="specie_open" class="specie_dropdown_menu">
                <div v-if="loading_species" class="loading">Loading...</div>
                <div v-else v-for="specie in species" :key="specie.name" class="specie_option" :class="{ selected: character?.species?.id === specie.id }" @click="pickSpecieToView(specie)">
                    {{ specie.name }}
                </div>
            </div>
        </div>

        <!--Detalles de la especie elegida en el desplegable-->
        <div v-if="viewed_loading" class="loading">Loading...</div>
        <div v-else-if="viewed_specie" class="specie_details_card">
            <div v-no-double-select class="specie_details_header">
                <span class="specie_details_name">{{ viewed_specie.name }}</span>
            </div>

            <span class="specie_card_expanded_walk">Movement: {{ viewed_specie.walkSpeed }}ft</span>
            <span v-if="viewed_specie.flySpeed > 0" class="specie_card_expanded_fly">Fly movement: {{ viewed_specie.flySpeed }}ft</span>
            
            <div v-if="viewed_specie.features?.length" class="specie_features">
                <h3>Features:</h3>
                <div v-for="f in viewed_specie.features" :key="f.id" class="specie_feature_row">
                    <div class="specie_feature_header" @click="toggleFeature(f.id)">
                        <span class="specie_feature_name">{{ f.name }}</span>
                        <button class="specie_feature_toggle_btn" :class="{ 'specie_feature_toggle_btn--open': expanded_features.has(f.id) }">
                            {{ expanded_features.has(f.id) ? '−' : '+' }}
                        </button>
                    </div>
                    <div v-if="expanded_features.has(f.id)" class="specie_feature_description" v-html="renderDescription(f.description)"></div>
                </div>
            </div>
            <button class="general_button specie_details_select_btn" @click="selectSpecie(viewed_id)" :disabled="saving || character?.species?.id === viewed_id">
                {{ saving ? 'Saving...' : (character?.species?.id === viewed_id ? 'Current Species' : `Pick ${viewed_specie.name}`) }}
            </button>
        </div>
    </CharacterStep>
</template>

<style scoped>
    /* Dentro de CharacterStep los hijos ocupan todo el ancho de la columna */
    h2 {
        text-align: center;
    }

    /* Cabecera en una sola fila y compacta */
    .character_specie_header {
        display: flex;
        align-items: center;
        justify-content: center;
        flex-wrap: wrap;
        gap: 6px 16px;
    }

    .character_specie_header h1 {
        margin: 0;
        font-size: 1.5rem;
    }

    /* El menú del desplegable flota encima del contenido en vez de empujarlo */
    .specie_filter {
        position: relative;
        align-self: center;
    }

    .specie_dropdown_menu {
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

    .specie_details_card {
        width: 100%;
        box-sizing: border-box;
    }

    /* Botón y descripción de las features (mismo aspecto que en clases) */
    .specie_feature_header {
        display: flex;
        align-items: center;
        justify-content: space-between;
        cursor: pointer;
    }

    .specie_feature_toggle_btn {
        width: 24px;
        height: 24px;
        border-radius: 50%;
        border: 1px solid var(--border);
        background: var(--surface);
        cursor: pointer;
        font-weight: bold;
        line-height: 1;
    }

    .specie_feature_toggle_btn--open {
        background: var(--accent-strong);
        color: var(--text-on-accent);
        border-color: var(--accent-strong-border);
    }

    .specie_feature_description {
        padding: 10px 12px;
        line-height: 1.5;
        font-size: 15px;
        border-top: 1px solid var(--separator-lines);
    }
</style>