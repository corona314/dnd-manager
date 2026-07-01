<script setup>
    import './styles/appClasses.css'
    import { ref, onMounted} from 'vue'
    const props = defineProps({ token: String })
    const API_BASE = 'http://localhost:8080/api'
    defineEmits(['back'])
    //Constantes
    const loading = ref(false)
    const classes_data = ref([])

    //Metodos
    async function fetchClasses() {
        loading.value = true
        try{        
            const res = await fetch(`${API_BASE}/classes`, {
            headers: { Authorization: `Bearer ${props.token}` }
            })
            const data = await res.json()
            classes_data.value = data.content

        }catch (e){
            console.error(e)
        }finally{
            loading.value = false
        }
    }
    onMounted(() => fetchClasses())
</script>

<template>
    <h1>Hello Hello</h1>
    <div v-if="loading" class="loading"> loading... </div>
    <div v-else class="classes_list">
        <div v-for="class_data in classes_data" :key="class_data.name" class="class_card">
            <div class="class_card_base">
                <span>{{ class_data.name }}</span>
            </div>
        </div>
    </div>
    <button @click="$emit('back')">Back</button>
</template>

<style>

</style>