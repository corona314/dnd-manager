<script setup>
    import '@/styles/compendium/appBackgrounds.css'
    import { ref, onMounted } from 'vue'
    import CompendiumCard from '@/vueComponents/shared/CompendiumCard.vue'
    import { useCardExpansion } from '@/composables/useCardExpansion.js'

    const props = defineProps({ token: String })
    const API_BASE = 'http://localhost:8080/api'
    defineEmits(['navigate'])

    //Constantes
    const loading = ref(false)
    const backgrounds = ref([])

    //Expansión de tarjetas (varias abiertas a la vez)
    const { isOpen, isLoading, details, toggle: expandBackground } =
        useCardExpansion('/backgrounds', () => props.token, { multiple: true })

    //Metodos
    async function fetchBackgrounds() {
        loading.value = true
        try {
            const res = await fetch(`${API_BASE}/backgrounds`, {
                headers: { Authorization: `Bearer ${props.token}` }
            })
            const data = await res.json()
            backgrounds.value = data.content
        } catch (e) {
            console.error(e)
        } finally {
            loading.value = false
        }
    }

    onMounted(() => fetchBackgrounds())
</script>

<template>
    <div class="compendium_page">
        <div v-blob-spot class="blob_spot_side"></div>
        <div v-if="loading" class="loading">Loading...</div>
        <div v-else class="compendium_scroll">
            <CompendiumCard
                v-for="background in backgrounds"
                :key="background.id"
                :expanded="isOpen(background.id)"
                @toggle="expandBackground(background)"
            >
                <template #base>
                    <div class="background_row">
                        <span>{{ background.name }}</span>
                    </div>
                </template>

                <template #expanded>
                    <div v-if="isLoading(background.id)">Loading...</div>
                    <div v-else-if="details[background.id]" class="card_expanded_content">
                        <span class="background_card_expanded_description">{{ background.description }}</span>

                        <div class="background_card_expanded_abilities">
                            <span
                                v-for="ability in details[background.id].abilities"
                                :key="ability"
                                class="background_card_expanded_ability"
                            >{{ ability }}</span>
                        </div>

                        <button class="general_button" @click="$emit('navigate', { page: 'backgroundExtended', backgroundId: background.id })">
                            More about {{ background.name }}
                        </button>
                    </div>
                </template>
            </CompendiumCard>
        </div>
    </div>
</template>

<style scoped>
    /* La base de CompendiumCard es una columna: aquí el nombre va centrado en vertical */
    .background_row {
        display: flex;
        flex: 1;
        align-items: center;
    }
</style>