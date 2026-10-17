<script setup>
    import '@/styles/compendium/appSpells.css'
    import '@/styles/character/appCharacterSpells.css'
    import CharacterStep from '@/vueComponents/shared/CharacterStep.vue'
    import SpellFilters from '@/vueComponents/shared/SpellFilters.vue'
    import SpellCard from '@/vueComponents/shared/SpellCard.vue'
    import { Components, Schools, createSpellFilters } from '@/composables/spellFilters.js'
    import { ref, reactive, computed, onMounted } from 'vue'
    import { useCardExpansion } from '@/composables/useCardExpansion.js'

    const props = defineProps({ token: String, characterId: { type: [Number, String], required: true } })
    const emit = defineEmits(['navigate'])
    const API_BASE = 'http://localhost:8080/api'

    const character = ref(null)
    const loading_character = ref(false)
    const error = ref('')

    const class_detail = ref(null)
    const loading_class = ref(false)

    // Recursos de conjuros
    const max_level = computed(() => {
        const slots = character.value?.spellSlots ?? []
        if (!slots.length) return 0
        return slots.at(-1).maxSlots > 0 ? slots.at(-1).spellLevel : 0
    })

    const cantripLimit = computed(() => {
        const res = character.value?.resources?.find(r => r.name === 'Cantrips')
        return res?.maxValue ?? 0
    })
    const canUseCantrips = computed(() => cantripLimit.value > 0)

    const preparedSpellsLimit = computed(() => {
        const res = character.value?.resources?.find(r => r.name === 'Prepared Spells')
        return res?.maxValue ?? 0
    })

    const availableCantrips = computed(() => {
        if (!canUseCantrips.value) return []
        return (class_detail.value?.spells ?? []).filter(s => s.level === 0)
    })

    const availableLeveledSpells = computed(() => {
        return (class_detail.value?.spells ?? []).filter(
            s => s.level >= 1 && s.level <= max_level.value
        )
    })

    // Pestaña: 'cantrip' o 'spell'
    const spell_tab = ref('cantrip')

    // Selección de conjuros
    const selected_spells = ref([])

    const selected_cantrips = computed(() => selected_spells.value.filter(s => s.level === 0))
    const selected_leveled_spells = computed(() => selected_spells.value.filter(s => s.level > 0))

    const canSelectMoreCantrips = computed(() => selected_cantrips.value.length < cantripLimit.value)
    const canSelectMoreSpells = computed(() => selected_leveled_spells.value.length < preparedSpellsLimit.value)

    function isSelected(spell) {
        return selected_spells.value.some(s => s.id === spell.id)
    }

    function canSelect(spell) {
        return spell.level === 0 ? canSelectMoreCantrips.value : canSelectMoreSpells.value
    }

    async function toggleSpell(spell) {
        if (isSelected(spell)) {
            // Optimista: lo quitamos ya de la lista local
            selected_spells.value = selected_spells.value.filter(s => s.id !== spell.id)
            try {
                await removeSpellFromCharacter(spell)
            } catch (e) {
                console.error(e)
                error.value = 'No se pudo eliminar el hechizo. Inténtalo de nuevo.'
                // Rollback: lo devolvemos a la lista si el backend falló
                selected_spells.value.push(spell)
            }
            return
        }

        if (!canSelect(spell)) return

        // Optimista: lo añadimos ya a la lista local
        selected_spells.value.push(spell)
        try {
            await addSpellToCharacter(spell)
        } catch (e) {
            console.error(e)
            error.value = 'It was not possible to add the spell. Please try again.'
            // Rollback: lo quitamos si el backend falló
            selected_spells.value = selected_spells.value.filter(s => s.id !== spell.id)
        }
    }

    async function addSpellToCharacter(spell) {
        const res = await fetch(`${API_BASE}/characters/${props.characterId}/spells/${spell.id}`, {
            method: 'POST',
            headers: {
                Authorization: `Bearer ${props.token}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ prepared: true, alwaysPrepared: false })
        })
        if (!res.ok) throw new Error(`Error al añadir hechizo (${res.status})`)
    }

    async function removeSpellFromCharacter(spell) {
        const res = await fetch(`${API_BASE}/characters/${props.characterId}/spells/${spell.id}`, {
            method: 'DELETE',
            headers: { Authorization: `Bearer ${props.token}` }
        })
        if (!res.ok) throw new Error(`Error al eliminar hechizo (${res.status})`)
    }

    // Filtros (aquí se aplican en memoria, no hace falta escuchar 'apply')
    const filters = reactive(createSpellFilters({ level: [1, 9] }))

    const filteredSpells = computed(() => {
        const isCantripTab = spell_tab.value === 'cantrip'
        const base = isCantripTab ? availableCantrips.value : availableLeveledSpells.value

        const name = filters.name.trim().toLowerCase()
        const [minL, maxL] = filters.level
        const schoolNames = filters.schools.map(id => Schools[id].toLowerCase())

        const list = base.filter(s => {
            if (name && !s.name.toLowerCase().includes(name)) return false

            // El nivel solo aplica a la pestaña de conjuros (los trucos son siempre nivel 0)
            if (!isCantripTab && (s.level < minL || s.level > maxL)) return false

            if (schoolNames.length && !schoolNames.includes((s.school ?? '').toLowerCase())) return false

            for (const c of Components) {
                const v = filters.components[c]
                const has = (s.components ?? '').includes(c)
                if (v === true && !has) return false
                if (v === false && has) return false
            }

            if (filters.ritual !== null && !!s.ritual !== filters.ritual) return false
            if (filters.concentration !== null && !!s.concentration !== filters.concentration) return false

            return true
        })

        const dir = filters.sortDirection === 'asc' ? 1 : -1
        const byName = (a, b) => a.name.localeCompare(b.name)
        const bySchool = (a, b) => (a.school ?? '').localeCompare(b.school ?? '')
        const byLevel = (a, b) => a.level - b.level

        return list.sort((a, b) => {
            switch (filters.sortField) {
                case 'level':  return dir * byLevel(a, b) || byName(a, b)
                case 'school': return dir * bySchool(a, b) || byLevel(a, b) || byName(a, b)
                default:       return dir * byName(a, b)
            }
        })
    })

    // Expansión de tarjetas
    const { isOpen, isLoading, details, toggle: expandSpell } = useCardExpansion('/spells', () => props.token, { multiple: true })

    // Carga de datos
    async function fetchCharacter() {
        loading_character.value = true
        try {
            const res = await fetch(`${API_BASE}/characters/${props.characterId}`, {
                headers: { Authorization: `Bearer ${props.token}` }
            })
            character.value = await res.json()

            if (character.value?.classes?.[0]?.classEntity?.id) {
                await fetchClassResource(character.value.classes[0].classEntity.id)
            }
        } catch (e) {
            console.error(e)
        } finally {
            loading_character.value = false
        }
    }

    async function fetchClassResource(classId) {
        loading_class.value = true
        try {
            const res = await fetch(`${API_BASE}/classes/${classId}`, {
                headers: { Authorization: `Bearer ${props.token}` }
            })
            class_detail.value = await res.json()
            filters.level = [1, Math.max(max_level.value, 1)]

            const classSpells = class_detail.value?.spells ?? []
            const characterSpellIds = new Set(
                (character.value?.spells ?? []).map(s => s.spell?.id ?? s.id)
            )

            selected_spells.value = classSpells.filter(s => characterSpellIds.has(s.id))
        } catch (e) {
            console.error(e)
        } finally {
            loading_class.value = false
        }
    }

    onMounted(() => {
        fetchCharacter()
    })

    function goBack() {
        emit('navigate', 'characterEquipment')
    }
</script>

<template>
    <CharacterStep
        :error="error"
        @back="goBack"
        @continue="emit('navigate', { page: 'characterFinalize', characterId: props.characterId })"
    >
        <!--Cabecera con info del personaje-->
        <template #header>
            <div v-if="loading_character">Loading Character...</div>
            <div v-else-if="character" class="character_spell_header">
                <h1>{{ character.name }}</h1>
            </div>
        </template>

        <!--Zona fija: filtros y pestañas (no hacen scroll)-->
        <template #toolbar>
            <template v-if="class_detail">
                <SpellFilters
                    v-model="filters"
                    :min-level="1"
                    :max-level="Math.max(max_level, 1)"
                    :show-level="spell_tab === 'spell' && max_level >= 1"
                />

                <div class="spell_tabs">
                    <button
                        :class="{ active: spell_tab === 'cantrip' }"
                        @click="spell_tab = 'cantrip'"
                        :disabled="!canUseCantrips"
                    >
                        Cantrips ({{ selected_cantrips.length }}/{{ cantripLimit }})
                    </button>
                    <button
                        :class="{ active: spell_tab === 'spell' }"
                        @click="spell_tab = 'spell'"
                    >
                        Spells ({{ selected_leveled_spells.length }}/{{ preparedSpellsLimit }})
                    </button>
                </div>
            </template>
        </template>

        <!--Lista de conjuros (esto es lo que hace scroll)-->
        <div v-if="class_detail" class="spell_list">
            <SpellCard
                v-for="spell in filteredSpells"
                :key="spell.id"
                :spell="spell"
                :expanded="isOpen(spell.id)"
                :detail="details[spell.id]"
                :loading="isLoading(spell.id)"
                selectable
                :selected="isSelected(spell)"
                :select-disabled="!isSelected(spell) && !canSelect(spell)"
                @expand="expandSpell"
                @toggle-select="toggleSpell"
            />
        </div>
    </CharacterStep>
</template>

<style scoped>
    /* Cabecera en una sola fila y compacta */
    .character_spell_header {
        display: flex;
        align-items: center;
        justify-content: center;
        flex-wrap: wrap;
        gap: 6px 16px;
    }

    .character_spell_header h1 {
        margin: 0;
        font-size: 1.5rem;
    }

    /* Pestañas: alineadas con la columna de la lista (75%) */
    .spell_tabs {
        display: flex;
        gap: 8px;
        width: 75%;
        margin: 10px 0;
        flex-shrink: 0;
    }

    .spell_tabs button {
        padding: 6px 14px;
        border: 1px solid var(--border);
        border-radius: 4px;
        background: var(--default);
        color: var(--text);
        cursor: pointer;
    }

    .spell_tabs button.active {
        background: var(--accent-bg);
        border-color: var(--accent-border);
    }

    .spell_tabs button:disabled {
        opacity: 0.4;
        cursor: default;
    }
</style>