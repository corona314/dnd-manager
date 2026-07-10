<script setup>
    import './styles/appClasses.css'
    import { ref, onMounted} from 'vue'
    const props = defineProps({ token: String })
    const API_BASE = 'http://localhost:8080/api'
    defineEmits(['navigate'])
    //Constantes
    const loading = ref(false)
    const classes_data = ref([])

    //Const expansión
    const expanded_class = ref(null)
    const expanded_loading = ref(false)
    const expanded_id = ref(null)

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

    async function expandedClass(classs){
        if (expanded_id.value === classs.id) {
            expanded_class.value = null
            expanded_id.value = null
            return
        }
        expanded_loading.value=true
        try{
            const res = await fetch(`${API_BASE}/classes/${classs.id}`, {
                headers: { Authorization: `Bearer ${props.token}` }
            })
            expanded_class.value = await res.json()
            expanded_id.value = classs.id
        }catch(e){
            console.error(e)
        }finally{
            expanded_loading.value=false
        }
    }
</script>

<template>
    <div class="classes_page">
        <h1>Class Compendium</h1>
        <div v-if="loading" class="loading"> loading... </div>
        <div v-else class="classes_list">
            <div v-for="class_data in classes_data" :key="class_data.name" class="class_card" @click="expandedClass(class_data)">
                <div class="class_card_base">
                    <span>{{ class_data.name }}</span>
                    <span class="class_card_base_hitpoint">Dice: {{ class_data.hitPointDie }}</span>
                </div>
                <div v-if="expanded_id===class_data.id" class="class_card_expanded" @click.stop>
                    <div v-if="expanded_loading">Cargando...</div>
                    <div v-else class="class_card_expanded_content">
                        <div v-if="expanded_class.subclasses?.length" class="comp_class_subclasses">
                            <span v-for="p in expanded_class.subclasses" :key="p.name" class="comp_subclasses_chip">
                                {{ p.name }}
                            </span>
                        </div>
                        <div v-if="expanded_class.savingThrows?.length" class="comp_class_saving_throws">
                            <span class="comp_class_saving_throws_label">Saving Throw Proficiencies:</span>
                            <span v-for="p in expanded_class.savingThrows" :key="p.ability" class="comp_class_saving_throws_chip">
                                {{ p.ability }}
                            </span>
                        </div>
                        <button @click="$emit('navigate', { page: 'classExtended', classId: class_data.id })">More about {{ class_data.name }}</button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<style>

</style>