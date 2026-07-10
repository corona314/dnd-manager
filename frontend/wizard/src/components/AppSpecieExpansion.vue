<script setup>
    import './styles/appSpecieExpansion.css'
    import { marked } from 'marked'
    import { ref, onMounted} from 'vue'
    const props = defineProps({ token: String, specieId: Number })
    const API_BASE = 'http://localhost:8080/api'
    defineEmits(['navigate'])
    //Constantes
    const loading = ref(true)
    const specie = ref(null)
    const expanded_features = ref(new Set())

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

    function toggleFeature(id) {
        if (expanded_features.value.has(id)) {
            expanded_features.value.delete(id)
        } else {
            expanded_features.value.add(id)
        }
    }

    function renderDescription(text) {
        if (!text) return ''
        return marked(text)
    }

</script>

<template>
    <div class="specie_page">
        <div v-if="loading" class="loading"> Cargando</div>
        <div v-else class="specie_info">
            <h1 class="specie_name">{{ specie.name }}</h1>
            <div v-if="specie.features?.length" class="specie_features">
                <div v-for="f in specie.features" :key="f.id" class="specie_feature_row">
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
    </div>
    
</template>

<style>

</style>