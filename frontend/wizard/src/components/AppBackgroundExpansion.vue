<script setup>
    import './styles/appClasses.css'
    import { ref, onMounted} from 'vue'
    const props = defineProps({ token: String, backgroundId: Number })
    const API_BASE = 'http://localhost:8080/api'
    defineEmits(['navigate'])
    //Constantes
    const loading = ref(true)
    const background = ref(null)

    //Metodos
    async function fetchBackgrounds() {
        loading.value = true
        try{        
            const res = await fetch(`${API_BASE}/backgrounds/${props.backgroundId}`, {
            headers: { Authorization: `Bearer ${props.token}` }
            })
            const data = await res.json()
            background.value = data

        }catch (e){
            console.error(e)
        }finally{
            loading.value = false
        }
    }

    onMounted(() => fetchBackgrounds())

</script>

<template>
    <div class="background_page">
        <div v-if="loading" class="loading"> Cargando</div>
        <div v-else class="background_info">
            <span>{{ background.name }}</span>
        </div>
    </div>
    
</template>

<style>

</style>