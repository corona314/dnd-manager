<script setup>
    import './styles/appCharacterBackground.css'
    import { ref, onMounted } from 'vue'
    const props = defineProps({ token: String, characterId: { type: [Number, String], required: true } })
    const emit = defineEmits(['navigate'])
    const API_BASE = 'http://localhost:8080/api'

    //Constantes del personaje
    const character = ref(null)
    const loading_character = ref(false)

    //Constantes de Especie
    const background = ref([])
    const loading_background = ref(false)
    const background_open = ref(false)

    //Const clase seleccionada (para ver detalles)
    const viewed_background = ref(null)
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

    async function fetchBackgrounds() {
        loading_background.value = true
        try{        
            const res = await fetch(`${API_BASE}/backgrounds`, {
            headers: { Authorization: `Bearer ${props.token}` }
            })
            const data = await res.json()
            background.value = data.content

        }catch (e){
            console.error(e)
        }finally{
            loading_background.value = false
        }
    }

    onMounted(() => {
        fetchBackgrounds()
        fetchCharacter()
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
            const res = await fetch(`${API_BASE}/characters/${props.characterId}`, {
                method: 'PATCH',
                headers: {
                    Authorization: `Bearer ${props.token}`,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ backgroundId: backgroundId })
            })
            if (!res.ok) {
                error.value = 'Error al guardar el trasfondo'
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
    <div class="character_background_page">
        <!--Cabecera con info del personaje-->
        <div v-if="loading_character">Cargando personaje...</div>
        <div v-else-if="character" class="character_background_header">
            <h1>{{ character.name }}</h1>
            <span v-if="character.background" class="character_backgroundcurrent">
                Current Background: {{ character.background.name }}
            </span>
            <span v-else class="character_background_current character_background_current--empty">
                No Background assigned
            </span>
        </div>

        <span v-if="error" class="character_background_error">{{ error }}</span>

        <!--Desplegable de clases-->
        <h2>Pick a Background</h2>
        <div class="background_filter">
            <div class="background_dropdown_btn" @click="background_open = !background_open">
                {{ viewed_background ? viewed_background.name : 'Selecciona un trasfondo' }} <i>▾</i>
            </div>
            <div v-if="background_open" class="background_dropdown_menu">
                <div v-if="loading_background" class="loading">Cargando...</div>
                <div v-else v-for="background in background" :key="background.name" class="background_option" :class="{ selected: character?.background?.id === background.id }" @click="pickBackgroundToView(background)">
                    {{ background.name }}
                </div>
            </div>
        </div>

        <!--Detalles de la clase elegida en el desplegable-->
        <div v-if="viewed_loading" class="loading">Cargando...</div>
        <div v-else-if="viewed_background" class="background_details_card">
            <div class="background_details_header">
                <span class="background_details_name">{{ viewed_background.name }}</span>
                <div></div>
                <span class="background_details_description">{{ viewed_background.description }}</span>
            </div>

            <button class="background_details_select_btn" @click="selectBackground(viewed_id)" :disabled="saving || character?.background?.id === viewed_id">
                {{ saving ? 'Guardando...' : (character?.background?.id === viewed_id ? 'Trasfondo actual' : `Elegir ${viewed_background.name}`) }}
            </button>
        </div>
        <button class="character_background_forward" @click="emit('navigate', {page: 'characterAbilities', characterId: props.characterId})">Continue Creation</button>
        <button class="character_background_back" @click="goBackToCharacters">Volver a mis personajes</button>
    </div>
</template>