<script setup>
    import './styles/appClasses.css'
    import { ref, onMounted} from 'vue'
    const props = defineProps({ token: String, classId: Number })
    const API_BASE = 'http://localhost:8080/api'
    defineEmits(['navigate'])
    //Constantes
    const loading = ref(true)
    const class_data = ref(null)

    //Metodos
    async function fetchClasses() {
        loading.value = true
        try{        
            const res = await fetch(`${API_BASE}/classes/${props.classId}`, {
            headers: { Authorization: `Bearer ${props.token}` }
            })
            const data = await res.json()
            class_data.value = data

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
            <span>{{ class_data.name }}</span>
            <div v-if="class_data.subclasses?.length" class="class_subclasses">
                <button v-for="p in class_data.subclasses" :key="p.name" class="class_subclasses_chip"  @click="$emit('navigate', { page: 'subclassExtended', subclassId: p.id })">
                    {{ p.name }}
                </button>
            </div>
            <div v-if="class_data.features?.length" class="class_features">
                <div v-for="feat in class_data.features" :key="feat.id" class="class_features_chip">
                    <span>{{ feat.feature.name }}*{{ feat.level }}</span>
                </div>
            </div>
        </div>
    </div>
    
</template>

<style>

</style>