<script setup>
    import './styles/appClasses.css'
    import { ref, onMounted} from 'vue'
    const props = defineProps({ token: String, subclassId: Number })
    const API_BASE = 'http://localhost:8080/api'
    defineEmits(['back'])
    //Constantes
    const loading = ref(true)
    const subclass= ref(null)

    //Metodos
    async function fetchClasses() {
        loading.value = true
        try{        
            const res = await fetch(`${API_BASE}/classes/subclasses/${props.subclassId}`, {
            headers: { Authorization: `Bearer ${props.token}` }
            })
            const data = await res.json()
            subclass.value = data

        }catch (e){
            console.error(e)
        }finally{
            loading.value = false
        }
    }

    onMounted(() => fetchClasses())

</script>

<template>
    <div class="class_page">
        <div v-if="loading" class="loading"> Cargando</div>
        <div v-else class="class_info">
            <span>{{ subclass.name }}</span>
            <div v-if="subclass.features?.length" class="class_features">
                <div v-for="feat in subclass.features" :key="feat.id" class="class_features_chip">
                    <span>{{ feat.feature.name }}*{{ feat.level }}</span>
                </div>
            </div>
        </div>
        <button @click="$emit('back')">Back</button>
    </div>
    
</template>

<style>

</style>