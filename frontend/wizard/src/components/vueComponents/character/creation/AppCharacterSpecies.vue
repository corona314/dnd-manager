<script setup>
    import '@/styles/character/appCharacterSpecies.css'
    import { ref, onMounted } from 'vue'
    import CharacterStep from '@/vueComponents/shared/CharacterStep.vue'
    import { renderDescription } from '@/composables/renderDescription.js'
    import CharacterPicker from '@/vueComponents/shared/CharacterPicker.vue'

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

    async function pickSpeciesToView(specie) {
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

    async function selectSpecies(specieId) {
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
            <div v-else-if="character" class="character_header">
                <h1>{{ character.name }}</h1>
                <span v-if="character.species" class="character_header_subtitle">
                    Current Species: {{ character.species.name }}
                </span>
                <span v-else class="character_specie_current character_specie_current--empty">
                    No Species assigned
                </span>
            </div>
        </template>


        <template #toolbar>
            <CharacterPicker
                noun="Species"
                :options="species"
                :loading="loading_species"
                :viewed-id="viewed_id"
                :viewed-name="viewed_specie?.name"
                :current-id="character?.species?.id"
                :saving="saving"
                @view="pickSpeciesToView"
                @pick="selectSpecies(viewed_id)"
            />
        </template>

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
        </div>
        <p v-else-if="!loading_character" class="empty_hint">Select a species from the list to see its details.</p>
    </CharacterStep>
</template>

<style scoped>

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