<script setup>
    import '@/styles/compendium/appSpecies.css'
    import { ref, onMounted } from 'vue'
    import CompendiumCard from '@/vueComponents/shared/CompendiumCard.vue'
    import { useCardExpansion } from '@/composables/useCardExpansion.js'

    const props = defineProps({ token: String })
    const API_BASE = 'http://localhost:8080/api'
    defineEmits(['navigate'])

    //Constantes
    const loading = ref(false)
    const species = ref([])

    //Expansión de tarjetas (varias abiertas a la vez)
    const { isOpen, isLoading, details, toggle: expandSpecie } =
        useCardExpansion('/species', () => props.token, { multiple: true })

    //Metodos
    async function fetchSpecies() {
        loading.value = true
        try {
            const res = await fetch(`${API_BASE}/species`, {
                headers: { Authorization: `Bearer ${props.token}` }
            })
            const data = await res.json()
            species.value = data.content
        } catch (e) {
            console.error(e)
        } finally {
            loading.value = false
        }
    }

    onMounted(() => fetchSpecies())
</script>

<template>
    <div class="compendium_page">
        <div v-if="loading" class="loading">Loading...</div>
        <div v-else class="compendium_scroll">
            <CompendiumCard
                v-for="specie in species"
                :key="specie.id"
                :expanded="isOpen(specie.id)"
                @toggle="expandSpecie(specie)"
            >
                <template #base>
                    <div class="specie_row">
                        <span>{{ specie.name }}</span>
                        <span>Size: {{ specie.size }}</span>
                    </div>
                </template>

                <template #expanded>
                    <div v-if="isLoading(specie.id)">Loading...</div>
                    <div v-else-if="details[specie.id]" class="card_expanded_content">
                        <span class="specie_card_expanded_walk">Movement: {{ details[specie.id].walkSpeed }}ft</span>
                        <span v-if="details[specie.id].flySpeed > 0" class="specie_card_expanded_fly">
                            Fly movement: {{ details[specie.id].flySpeed }}ft
                        </span>

                        <button class="general_button" @click="$emit('navigate', { page: 'specieExtended', specieId: specie.id })">
                            More about {{ specie.name }}
                        </button>
                    </div>
                </template>
            </CompendiumCard>
        </div>
    </div>
</template>

<style scoped>
    /* La base de CompendiumCard es una columna: aquí el nombre y el tamaño van en fila */
    .specie_row {
        display: flex;
        flex: 1;
        justify-content: space-between;
        align-items: center;
    }
</style>