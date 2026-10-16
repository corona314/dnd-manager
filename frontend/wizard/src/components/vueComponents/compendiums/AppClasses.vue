<script setup>
    import '@/styles/compendium/appClasses.css'
    import { ref, onMounted } from 'vue'
    import CompendiumCard from '@/vueComponents/shared/CompendiumCard.vue'
    import { useCardExpansion } from '@/composables/useCardExpansion.js'
    import DiceIcon from '@/icons/DiceIcon.vue'
    import { usingIcons } from '@/composables/usePreferences.js'
    
    const props = defineProps({ token: String })
    const API_BASE = 'http://localhost:8080/api'
    defineEmits(['navigate'])

    //Constantes
    const loading = ref(false)
    const classes_data = ref([])

    //Expansión de tarjetas (varias abiertas a la vez)
    const { isOpen, isLoading, details, toggle: expandClass } =
        useCardExpansion('/classes', () => props.token, { multiple: true })

    //Metodos
    async function fetchClasses() {
        loading.value = true
        try {
            const res = await fetch(`${API_BASE}/classes`, {
                headers: { Authorization: `Bearer ${props.token}` }
            })
            const data = await res.json()
            classes_data.value = data.content
        } catch (e) {
            console.error(e)
        } finally {
            loading.value = false
        }
    }

    onMounted(() => fetchClasses())
</script>

<template>
    <div class="compendium_page">
        <div v-blob-spot class="blob_spot_side"></div>
        <h1 class="classes_title">Class Compendium</h1>

        <div v-if="loading" class="loading">Loading...</div>
        <div v-else class="compendium_scroll">
            <CompendiumCard
                v-for="class_data in classes_data"
                :key="class_data.id"
                :expanded="isOpen(class_data.id)"
                @toggle="expandClass(class_data)"
            >
                <template #base>
                    <div class="class_row">
                        <span>{{ class_data.name }}</span>
                        <span class="class_card_base_hitpoint" title="Hit Point Die">
                            <DiceIcon v-if="usingIcons" />
                            <template v-else>Dice:</template>
                            {{ class_data.hitPointDie }}
                        </span>                    
                    </div>
                </template>

                <template #expanded>
                    <div v-if="isLoading(class_data.id)">Loading...</div>
                    <div v-else-if="details[class_data.id]" class="card_expanded_content">
                        <div v-if="details[class_data.id].subclasses?.length" class="comp_class_subclasses">
                            <span v-for="p in details[class_data.id].subclasses" :key="p.name" class="comp_subclasses_chip">
                                {{ p.name }}
                            </span>
                        </div>

                        <div v-if="details[class_data.id].savingThrows?.length" class="comp_class_saving_throws">
                            <span class="comp_class_saving_throws_label">Saving Throw Proficiencies:</span>
                            <span v-for="p in details[class_data.id].savingThrows" :key="p.ability" class="comp_class_saving_throws_chip">
                                {{ p.ability }}
                            </span>
                        </div>

                        <button class="general_button" @click="$emit('navigate', { page: 'classExtended', classId: class_data.id })">
                            More about {{ class_data.name }}
                        </button>
                    </div>
                </template>
            </CompendiumCard>
        </div>
    </div>
</template>

<style scoped>
    .classes_title {
        flex-shrink: 0;
        margin: 20px 0 10px;
    }

    /* La base de CompendiumCard es una columna: aquí la queremos en fila */
    .class_row {
        display: flex;
        flex: 1;
        justify-content: space-between;
        align-items: center;
    }
</style>