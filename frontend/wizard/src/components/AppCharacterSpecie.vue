<script setup>
    import './styles/appCharacterSpecie.css'
    import { ref, onMounted } from 'vue'
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

    onMounted(() => {
        fetchSpecies()
        fetchCharacter()
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
                error.value = 'Error al guardar la especie'
                return
            }
            await fetchCharacter()
        } catch (e) {
            console.error(e)
            error.value = 'Error de conexión'
        } finally {
            saving.value = false
        }
    }

    function goBackToCharacters() {
        emit('navigate', 'characters')
    }
</script>

<template>
    <div class="character_specie_page">
        <!--Cabecera con info del personaje-->
        <div v-if="loading_character">Cargando personaje...</div>
        <div v-else-if="character" class="character_specie_header">
            <h1>{{ character.name }}</h1>
            <span v-if="character.species" class="character_specie_current">
                Current Specie: {{ character.species.name }}
            </span>
            <span v-else class="character_specie_current character_specie_current--empty">
                No Specie assigned
            </span>
        </div>

        <span v-if="error" class="character_specie_error">{{ error }}</span>

        <!--Desplegable de clases-->
        <h2>Pick a Specie</h2>
        <div class="specie_filter">
            <div class="specie_dropdown_btn" @click="specie_open = !specie_open">
                {{ viewed_specie ? viewed_specie.name : 'Selecciona una especie' }} <i>▾</i>
            </div>
            <div v-if="specie_open" class="specie_dropdown_menu">
                <div v-if="loading_species" class="loading">Cargando...</div>
                <div v-else v-for="specie in species" :key="specie.name" class="specie_option" :class="{ selected: character?.specie?.id === specie.id }" @click="pickSpecieToView(specie)">
                    {{ specie.name }}
                </div>
            </div>
        </div>

        <!--Detalles de la clase elegida en el desplegable-->
        <div v-if="viewed_loading" class="loading">Cargando...</div>
        <div v-else-if="viewed_specie" class="specie_details_card">
            <div class="specie_details_header">
                <span class="specie_details_name">{{ viewed_specie.name }}</span>
            </div>

            <button class="specie_details_select_btn" @click="selectSpecie(viewed_id)" :disabled="saving || character?.species?.id === viewed_id">
                {{ saving ? 'Guardando...' : (character?.species?.id === viewed_id ? 'Especie actual' : `Elegir ${viewed_specie.name}`) }}
            </button>
        </div>
        <button class="character_specie_forward" @click="emit('navigate', {page: 'characterBackground', characterId: props.characterId})" :disabled="character?.species === null">Continue Creation</button>
        <button class="character_specie_back" @click="goBackToCharacters">Volver a mis personajes</button>
    </div>
</template>

<style>

</style>