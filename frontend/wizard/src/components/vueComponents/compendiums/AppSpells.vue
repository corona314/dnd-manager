<script setup>
    import '@/styles/appSpells.css'
    import SpellFilters from '@/vueComponents/shared/SpellFilters.vue'
    import SpellCard from '@/vueComponents/shared/SpellCard.vue'
    import { createSpellFilters } from '@/composables/spellFilters.js'
    import { useCardExpansion } from '@/composables/useCardExpansion.js'
    import { ref, reactive, onMounted } from 'vue'

    const props = defineProps({ token: String })
    const API_BASE = 'http://localhost:8080/api'

    // Filtros (el panel los edita; aquí solo se leen para pedir datos)
    const filters = reactive(createSpellFilters({ level: [0, 9] }))
    const filters_open = ref(true)

    // Lista de conjuros
    const spells = ref([])
    const loading = ref(false)

    // Expansión de tarjetas
    const { isOpen, isLoading, details, toggle: expandSpell, collapseAll } = useCardExpansion('/spells', () => props.token, { multiple: true })

    // Paginación
    const current_page = ref(0)
    const total_pages = ref(1)

    async function fetchSpells(page = 0) {
        loading.value = true
        try {
            const params = new URLSearchParams({ page, size: 20 })

            params.append('sort', `${filters.sortField},${filters.sortDirection}`)

            if (filters.sortField === 'level') {
                params.append('sort', 'name,asc')
            }

            if (filters.sortField === 'school') {
                params.append('sort', 'level,asc')
                params.append('sort', 'name,asc')
            }

            if (filters.name) params.append('name', filters.name)

            if (filters.level[0] !== null) params.append('levelMin', filters.level[0])
            if (filters.level[1] !== null) params.append('levelMax', filters.level[1])

            for (const schoolId of filters.schools) {
                params.append('schoolId', schoolId)
            }

            for (const component in filters.components) {
                const val = filters.components[component]
                if (val === true) params.append('components', component)
                if (val === false) params.append('components', `!${component}`)
            }

            if (filters.ritual === true) params.append('ritual', '1')
            if (filters.ritual === false) params.append('ritual', '0')

            if (filters.concentration === true) params.append('concentration', '1')
            if (filters.concentration === false) params.append('concentration', '0')

            const res = await fetch(`${API_BASE}/spells?${params}`, {
                headers: { Authorization: `Bearer ${props.token}` }
            })
            const data = await res.json()

            spells.value = data.content
            total_pages.value = data.totalPages
            current_page.value = data.number
        } catch (e) {
            console.error(e)
        } finally {
            loading.value = false
        }
    }

    onMounted(() => fetchSpells(0))

    function applyFilters() {
        collapseAll()
        fetchSpells(0)
    }

    function goToPage(page) {
        collapseAll()
        if (page < 0 || page >= total_pages.value) return
        fetchSpells(page)
    }
</script>

<template>
    <div class="compendium_page">
        <!--Filtros de selección-->
        <SpellFilters
            v-model="filters"
            v-model:open="filters_open"
            show-search-button
            @apply="applyFilters"
        />


        <!--Tarjetas de los conjuros-->
        <div v-if="loading" class="loading">Loading...</div>
        <div v-else class="compendium_scroll" :class="{ 'spell_list--filters-closed': !filters_open }">
            
            <SpellCard
                v-for="spell in spells"
                :key="spell.id"
                :spell="spell"
                :expanded="isOpen(spell.id)"
                :detail="details[spell.id]"
                :loading="isLoading(spell.id)"
                @expand="expandSpell"
            />
        </div>

        <!--Selector de página-->
        <div class="compendium_pages">
            <button
                class="compendium_page_button page_button--first"
                @click="goToPage(0)"
                :disabled="current_page === 0"
                title="Primera página"
            >
                «
            </button>

            <button
                class="compendium_page_button page_button"
                @click="goToPage(current_page - 1)"
                :disabled="current_page === 0"
                title="Página anterior"
            >
                ‹
            </button>

            <span class="page_info">
                Page {{ current_page + 1 }} / {{ total_pages }}
            </span>

            <button
                class="compendium_page_button "
                @click="goToPage(current_page + 1)"
                :disabled="current_page >= total_pages - 1"
                title="Página siguiente"
            >
                ›
            </button>

            <button
                class="compendium_page_button page_button--last"
                @click="goToPage(total_pages - 1)"
                :disabled="current_page >= total_pages - 1"
                title="Última página"
            >
                »
            </button>
        </div>
    </div>
</template>