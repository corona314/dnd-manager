<script setup>
    import './styles/appBackgrounds.css'
    import { ref, onMounted} from 'vue'
    const props = defineProps({ token: String })
    const API_BASE = 'http://localhost:8080/api'
    defineEmits(['navigate'])
    //Constantes
    const loading = ref(false)
    const backgrounds = ref([])

    //Const expansión
    const expanded_background = ref(null)
    const expanded_loading = ref(false)
    const expanded_id = ref(null)

    //Metodos
    async function fetchBackgrounds() {
        loading.value = true
        try{        
            const res = await fetch(`${API_BASE}/backgrounds`, {
            headers: { Authorization: `Bearer ${props.token}` }
            })
            const data = await res.json()
            backgrounds.value = data.content

        }catch (e){
            console.error(e)
        }finally{
            loading.value = false
        }
    }

    onMounted(() => fetchBackgrounds())

    async function expandedBackground(background){
        if (expanded_id.value === background.id) {
            expanded_background.value = null
            expanded_id.value = null
            return
        }
        expanded_loading.value=true
        try{
            const res = await fetch(`${API_BASE}/backgrounds/${background.id}`, {
                headers: { Authorization: `Bearer ${props.token}` }
            })
            expanded_background.value = await res.json()
            expanded_id.value = background.id
        }catch(e){
            console.error(e)
        }finally{
            expanded_loading.value=false
        }
    }
</script>

<template>
    <div class="backgrounds_page">
        <div v-if="loading" class="loading"> loading... </div>
        <div v-else class="backgrounds_list">
            <div v-for="background in backgrounds" :key="background.name" class="background_card" @click="expandedBackground(background)">
                <div class="background_card_base">
                    <span>{{ background.name }}</span>
                </div>
                <div v-if="expanded_id===background.id" class="background_card_expanded" @click.stop>
                    <div v-if="expanded_loading">Cargando...</div>
                    <div v-else class="background_card_expanded_content">
                        <span class="background_card_expanded_description">{{ background.description }}</span>
                        <div class="background_card_expanded_abilities">
                            <span v-for="ability in expanded_background.abilities" :key="ability" class="background_card_expanded_ability"> {{ ability }} </span>
                        </div>
                        <button @click="$emit('navigate', { page: 'backgroundExtended', backgroundId: background.id })">More about {{ background.name }}</button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<style>
    
</style>