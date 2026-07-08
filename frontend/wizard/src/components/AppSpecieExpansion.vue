<script setup>
    import './styles/appSpecieExpansion.css'
    import { ref, onMounted} from 'vue'
    const props = defineProps({ token: String, specieId: Number })
    const API_BASE = 'http://localhost:8080/api'
    defineEmits(['navigate'])
    //Constantes
    const loading = ref(true)
    const specie = ref(null)

    //Metodos
    async function fetchSpecies() {
        loading.value = true
        try{        
            const res = await fetch(`${API_BASE}/species/${props.specieId}`, {
            headers: { Authorization: `Bearer ${props.token}` }
            })
            const data = await res.json()
            specie.value = data

        }catch (e){
            console.error(e)
        }finally{
            loading.value = false
        }
    }

    onMounted(() => fetchSpecies())

</script>

<template>
    <div class="specie_page">
        <div v-if="loading" class="loading"> Cargando</div>
        <div v-else class="specie_info">
            <span>{{ specie.name }}</span>
        </div>
    </div>
    
</template>

<style>

</style>