<script setup>
    //import './styles/appCharacterSubclass.css'
    import { ref, onMounted } from 'vue'
    const props = defineProps({ token: String, characterId: { type: [Number, String], required: true } })
    const emit = defineEmits(['navigate'])
    const API_BASE = 'http://localhost:8080/api'

    //Constantes del personaje
    const character = ref(null)
    const loading_character = ref(false)

    //Constantes de subclases
    const subclasses_data = ref([])
    const loading_subclasses = ref(false)
    const subclass_open = ref(false)
 
    //Const subclase seleccionada (para ver detalles)
    const viewed_subclass = ref(null)
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

            if (character.value?.classEntity?.id) {
                await fetchSubclasses(character.value.classEntity.id)
            }
        } catch (e) {
            console.error(e)
        } finally {
            loading_character.value = false
        }
    }

    async function fetchSubclasses(classId) {
        loading_subclasses.value = true
        try {
            const res = await fetch(`${API_BASE}/classes/${classId}`, {
                headers: { Authorization: `Bearer ${props.token}` }
            })
            const data = await res.json()
            subclasses_data.value = data.subclasses ?? []
        } catch (e) {
            console.error(e)
        } finally {
            loading_subclasses.value = false
        }
    }


    onMounted(() => {
        fetchCharacter()
    })

    async function pickSubclassToView(subclass_data) {
        subclass_open.value = false
        viewed_loading.value = true
        try{
            const res = await fetch(`${API_BASE}/classes/subclasses/${subclass_data.id}`, {
                headers: { Authorization: `Bearer ${props.token}` }
            })
            viewed_subclass.value = await res.json()
            viewed_id.value = subclass_data.id
        }catch(e){
            console.error(e)
        }finally{
            viewed_loading.value = false
        }
    }
 
    async function selectSubclass(subclassId) {
        saving.value = true
        error.value = ''
        try {
            const res = await fetch(`${API_BASE}/characters/${props.characterId}`, {
                method: 'PATCH',
                headers: {
                    Authorization: `Bearer ${props.token}`,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ subclassId })
            })
            if (!res.ok) {
                error.value = 'Error al guardar la subclase'
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

    function featuresByLevel() {
        if (!viewed_subclass.value?.features) return []
        const levels = [...new Set(viewed_subclass.value.features.map(f => f.level))].sort((a, b) => a - b)
        return levels.map(level => ({
            level,
            features:viewed_subclass.value.features.filter(f => f.level === level)
        }))
    }

    function goBackToLevelUp(){
        emit('navigate', 'characterLevelUp')
    }

    function goBackToCharacters() {
        emit('navigate', 'characters')
    }
</script>

<template>
    <div class="character_subclass_page">
        <!--Cabecera con info del personaje-->
        <div v-if="loading_character">Cargando personaje...</div>
        <div v-else-if="character" class="character_subclass_header">
            <h1>{{ character.name }}</h1>
            <span class="character_subclass_class_label">
                Clase: {{ character.classEntity?.name ?? 'Sin asignar' }}
            </span>
            <span v-if="character.subclass" class="character_subclass_current">
                Current Subclass: {{ character.subclass.name }}
            </span>
            <span v-else class="character_subclass_current character_subclass_current--empty">
                No Subclass assigned
            </span>
        </div>
 
        <span v-if="!character?.classEntity" class="character_subclass_warning">
            Debes elegir una clase antes de poder elegir una subclase.
        </span>
 
        <span v-if="error" class="character_subclass_error">{{ error }}</span>
 
        <!--Desplegable de subclases-->
        <h2>Pick a Subclass</h2>
        <div class="subclass_filter">
            <div class="subclass_dropdown_btn" @click="subclass_open = !subclass_open">
                {{ viewed_subclass ? viewed_subclass.name : 'Selecciona una subclase' }} <i>▾</i>
            </div>
            <div v-if="subclass_open" class="subclass_dropdown_menu">
                <div v-if="loading_subclasses" class="loading">Cargando...</div>
                <div v-else v-for="subclass_data in subclasses_data" :key="subclass_data.id" class="subclass_option" :class="{ selected: character?.subclass?.id === subclass_data.id }" @click="pickSubclassToView(subclass_data)">
                    {{ subclass_data.name }}
                </div>
            </div>
        </div>
 
        <!--Detalles de la subclase elegida en el desplegable-->
        <div v-if="viewed_loading" class="loading">Cargando...</div>
        <div v-else-if="viewed_subclass" class="subclass_details_card">
            <div class="subclass_details_header">
                <span class="subclass_details_name">{{ viewed_subclass.name }}</span>
            </div>
 
            <div v-if="viewed_subclass.features?.length" class="subclass_features_section">
                <h3>Feats by level:</h3>
                <div v-for="group in featuresByLevel()" :key="group.level" class="subclass_feature_level_group">
                    <h4 class="subclass_feature_level_title">Nivel {{ group.level }}</h4>
                    <div v-for="f in group.features" :key="f.feature.id" class="subclass_feature_row">
                        <div class="subclass_feature_header">
                            <span class="subclass_feature_name">{{ f.feature.name }}</span>
                        </div>
                    </div>
                </div>
            </div>
 
            
            <button class="subclass_details_select_btn" @click="selectSubclass(viewed_id)" :disabled="saving || character?.subclass?.id === viewed_id">
                {{ saving ? 'Guardando...' : (character?.subclass?.id === viewed_id ? 'Subclase actual' : `Elegir ${viewed_subclass.name}`) }}
            </button>
        </div>

        <button class="character_subclass_forward" @click="goBackToLevelUp">Continue Level Up</button>
        <button class="character_subclass_back" @click="goBackToCharacters">Volver a mis personajes</button>
    </div>
</template>

<style>

</style>