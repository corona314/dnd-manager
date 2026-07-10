<script setup>
    import './styles/appSpecies.css'
    import { ref, onMounted} from 'vue'
    const props = defineProps({ token: String })
    const API_BASE = 'http://localhost:8080/api'
    defineEmits(['navigate'])
    //Constantes
    const loading = ref(false)
    const species = ref([])

    //Const expansión
    const expanded_specie = ref(null)
    const expanded_loading = ref(false)
    const expanded_id = ref(null)

    //Metodos
    async function fetchSpecies() {
        loading.value = true
        try{        
            const res = await fetch(`${API_BASE}/species`, {
            headers: { Authorization: `Bearer ${props.token}` }
            })
            const data = await res.json()
            species.value = data.content

        }catch (e){
            console.error(e)
        }finally{
            loading.value = false
        }
    }

    onMounted(() => fetchSpecies())

    async function expandedSpecie(specie){
        if (expanded_id.value === specie.id) {
            expanded_specie.value = null
            expanded_id.value = null
            return
        }
        expanded_loading.value=true
        try{
            const res = await fetch(`${API_BASE}/species/${specie.id}`, {
                headers: { Authorization: `Bearer ${props.token}` }
            })
            expanded_specie.value = await res.json()
            expanded_id.value = specie.id
        }catch(e){
            console.error(e)
        }finally{
            expanded_loading.value=false
        }
    }
</script>

<template>
    <div class="species_page">
        <div v-if="loading" class="loading"> loading... </div>
        <div v-else class="species_list">
            <div v-for="specie in species" :key="specie.name" class="specie_card" @click="expandedSpecie(specie)">
                <div class="specie_card_base">
                    <span>{{ specie.name }}</span>
                    <span>Size: {{ specie.size }}</span>
                </div>
                <div v-if="expanded_id===specie.id" class="specie_card_expanded" @click.stop>
                    <div v-if="expanded_loading">Cargando...</div>
                    <div v-else class="specie_card_expanded_content">
                        <span class="specie_card_expanded_walk">Movement: {{ expanded_specie.walkSpeed }}ft</span>
                        <span v-if="expanded_specie.flySpeed !== 0" class="specie_card_expanded_fly">Fly movement: {{ expanded_specie.flySpeed }}ft</span>
                        <button @click="$emit('navigate', { page: 'specieExtended', specieId: specie.id })">More about {{ specie.name }}</button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<style>
    
</style>