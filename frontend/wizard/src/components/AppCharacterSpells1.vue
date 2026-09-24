<script setup>
    import './styles/appSpells.css'
    import './styles/appCharacterSpells.css'
    import Slider from 'primevue/slider'
    import RitualIcon from './icons/RitualIcon.vue'
    import ConcentrationIcon from './icons/ConcentrationIcon.vue'
    import { ref, computed, onMounted } from 'vue'
    import { marked } from 'marked'
    const props = defineProps({ token: String, characterId: { type: [Number, String], required: true } })
    const emit = defineEmits(['navigate'])
    const API_BASE = 'http://localhost:8080/api'

    const character = ref(null)
    const loading_character = ref(false)
    const error = ref('')
    
    const class_detail = ref(null)
    const loading_class = ref(false)

    //Const Spell resources
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

    // Seleccion de pestaña: 'cantrip' o 'spell'
    const spell_tab = ref('cantrip')

    const visibleSpells = computed(() => {return spell_tab.value === 'cantrip' ? availableCantrips.value : availableLeveledSpells.value})

    // Seleccion de conjuros
    const selected_spells = ref([]) 

    const selected_cantrips = computed(() =>
        selected_spells.value.filter(s => s.level === 0)
    )
    const selected_leveled_spells = computed(() =>
        selected_spells.value.filter(s => s.level > 0)
    )

    const canSelectMoreCantrips = computed(() =>
        selected_cantrips.value.length < cantripLimit.value
    )
    const canSelectMoreSpells = computed(() =>
        selected_leveled_spells.value.length < preparedSpellsLimit.value
    )

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
            error.value = 'No se pudo añadir el hechizo. Inténtalo de nuevo.'
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

    //Filtrado de conjuros
    const Components = ['V', 'S', 'M']
    const Schools = { 1: 'Abjuration', 2: 'Conjuration', 3: 'Divination', 4: 'Enchantment', 5: 'Evocation', 6: 'Illusion', 7: 'Necromancy', 8: 'Transmutation' }

    const filter_name = ref('')
    const filter_level = ref([1, 9])
    const prev_level = ref([1, 9])
    const filter_school = ref([])
    const filter_components = ref({ V: null, S: null, M: null })
    const filter_ritual = ref(null)
    const filter_concentration = ref(null)
    const school_open = ref(false)
    const filters_open = ref(true)

    const sort_labels = { name: 'Name', level: 'Level', school: 'School' }
    const sort_field = ref('name')
    const sort_direction = ref('asc')
    const sort_open = ref(false)

    function setSortField(field) {
        sort_field.value = field
        sort_open.value = false
    }
    function toggleSortDirection() {
        sort_direction.value = sort_direction.value === 'asc' ? 'desc' : 'asc'
    }

    function nextTristate(v) {
        if (v === null) return true
        if (v === true) return false
        return null
    }
    function previousTristate(v) {
        if (v === null) return false
        if (v === false) return true
        return null
    }
    function cycleComponent(c) { filter_components.value[c] = nextTristate(filter_components.value[c]) }
    function previousComponent(c) { filter_components.value[c] = previousTristate(filter_components.value[c]) }
    function cycleRitual() { filter_ritual.value = nextTristate(filter_ritual.value) }
    function previousRitual() { filter_ritual.value = previousTristate(filter_ritual.value) }
    function cycleConcentration() { filter_concentration.value = nextTristate(filter_concentration.value) }
    function previousConcentration() { filter_concentration.value = previousTristate(filter_concentration.value) }

    function toggleSchool(id) {
        const i = filter_school.value.indexOf(id)
        if (i === -1) filter_school.value.push(id)
        else filter_school.value.splice(i, 1)
    }

    function sliderOrder(value) {
        const [newMin, newMax] = value
        const [oldMin, oldMax] = prev_level.value
        let min = newMin
        let max = newMax
        if (newMin !== oldMin) {
            min = Math.min(newMin, oldMax)
            max = oldMax
        } else if (newMax !== oldMax) {
            max = Math.max(newMax, oldMin)
            min = oldMin
        }
        filter_level.value = [min, max]
        prev_level.value = [min, max]
    }

    function minMaxFilters() { filters_open.value = !filters_open.value }

    const filteredSpells = computed(() => {
        const isCantripTab = spell_tab.value === 'cantrip'
        const base = isCantripTab ? availableCantrips.value : availableLeveledSpells.value

        const name = filter_name.value.trim().toLowerCase()
        const [minL, maxL] = filter_level.value
        const schoolNames = filter_school.value.map(id => Schools[id].toLowerCase())

        const list = base.filter(s => {
            if (name && !s.name.toLowerCase().includes(name)) return false

            // El nivel solo aplica a la pestaña de conjuros (los trucos son siempre nivel 0)
            if (!isCantripTab && (s.level < minL || s.level > maxL)) return false

            if (schoolNames.length && !schoolNames.includes((s.school ?? '').toLowerCase())) return false

            for (const c of Components) {
                const v = filter_components.value[c]
                const has = (s.components ?? '').includes(c)
                if (v === true && !has) return false
                if (v === false && has) return false
            }

            if (filter_ritual.value !== null && !!s.ritual !== filter_ritual.value) return false
            if (filter_concentration.value !== null && !!s.concentration !== filter_concentration.value) return false

            return true
        })

        const dir = sort_direction.value === 'asc' ? 1 : -1
        const byName = (a, b) => a.name.localeCompare(b.name)
        const bySchool = (a, b) => (a.school ?? '').localeCompare(b.school ?? '')
        const byLevel = (a, b) => a.level - b.level

        return list.sort((a, b) => {
            switch (sort_field.value) {
                case 'level':  return dir * byLevel(a, b) || byName(a, b)
                case 'school': return dir * bySchool(a, b) || byLevel(a, b) || byName(a, b)
                default:       return dir * byName(a, b)
            }
        })
    })


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

    // Spell Expansion
    const expanded_spell = ref(null)
    const expanded_loading = ref(false)
    const expanded_id = ref(null)
    const selected_upcast_level = ref(null)

    async function expandSpell(spell) {
        if (expanded_id.value === spell.id) {
            expanded_spell.value = null
            expanded_id.value = null
            return
        }
        expanded_loading.value = true
        try {
            const res = await fetch(`${API_BASE}/spells/${spell.id}`, {
                headers: { Authorization: `Bearer ${props.token}` }
            })
            expanded_spell.value = await res.json()
            expanded_id.value = spell.id
            selected_upcast_level.value = expanded_spell.value.level + 1
        } catch (e) {
            console.error(e)
        } finally {
            expanded_loading.value = false
        }
    }

    function selectUpcastLevel(level) {
        selected_upcast_level.value = level
    }

    function getUpcastText(spell, level) {
        if (level === spell.level) {
            return spell.damageRoll ? `${spell.damageRoll} ${spell.damageTypes[0]?.damageType ?? ''}` : null
        }
        const upcast = spell.upcasts.find(u => u.level === level)
        if (!upcast) return null
        if (upcast.description) return upcast.description
        if (upcast.damageRoll) return `${upcast.damageRoll} ${spell.damageTypes[0]?.damageType + ' damage' ?? ''}`
        return null
    }

    function renderDescription(text) {
        if (!text) return ''
        const fixed = text
            .replace(/\|\s*\|/g, '|\n|')
            .replace(/(Table:[^\n|]+)\|/, '$1\n|')
        return marked(fixed)
    }


    //TO-DO Metodo obtener recursos
    async function fetchClassResource(classId) {
        loading_class.value = true
        try {
            const res = await fetch(`${API_BASE}/classes/${classId}`, {
                headers: { Authorization: `Bearer ${props.token}` }
            })
            class_detail.value = await res.json()
            filter_level.value = [1, Math.max(max_level.value, 1)]
            prev_level.value = [1, Math.max(max_level.value, 1)]

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

    function goBackToCharacters() {
        emit('navigate', 'characters')
    }
</script>

<template>
    <div class="character_spell_page">
        <div v-if="loading_character">Cargando personaje...</div>

        <div v-else-if="character" class="character_spell_summary">
            <h1>{{ character.name }}</h1>
        </div>

        <span v-if="error" class="character_spell_error">{{ error }}</span>

        <div v-if="class_detail" class="spell_page">

            <div class="filters_character" :class="{ 'filters_character--open': filters_open, 'filters_character--closed': !filters_open }">
                <div class="name">
                    <input class="name_input" type="text" placeholder="Search spell..." v-model="filter_name" />
                </div>

                <div class="level" v-if="spell_tab === 'spell' && max_level >= 1">
                    <span class="level_label">{{ filter_level[0] }} -- {{ filter_level[1] }}</span>
                    <Slider class="level_slider" v-model="filter_level" :min="1" :max="max_level" :step="1" range @update:modelValue="sliderOrder" />
                </div>

                <div class="sort_filter">
                    <span class="sort_title">Order by:</span>
                    <div class="sort_dropdown">
                        <div class="dropdown_btn" @click="sort_open = !sort_open">
                            {{ sort_labels[sort_field] }} <i>-</i>
                        </div>
                        <div v-if="sort_open" class="dropdown_menu">
                            <div
                                v-for="(label, field) in sort_labels"
                                :key="field"
                                class="dropdown_option"
                                :class="{ selected: sort_field === field }"
                                @click="setSortField(field)"
                            >{{ label }}</div>
                        </div>
                    </div>
                    <button class="sort_direction" @click="toggleSortDirection">
                        {{ sort_direction === 'asc' ? '▲' : '▼' }}
                    </button>
                </div>

                <div class="filter_group components_group">
                    <span
                        v-for="comp in Components"
                        :key="comp"
                        class="tristate"
                        @click="cycleComponent(comp)"
                        @contextmenu.prevent="previousComponent(comp)"
                        :class="{
                            'tristate--active': filter_components[comp] === true,
                            'tristate--inactive': filter_components[comp] === false
                        }"
                    >{{ comp }}</span>
                </div>

                <div class="filter_group special_group">
                    <span
                        class="tristate"
                        @click="cycleRitual"
                        @contextmenu.prevent="previousRitual"
                        :class="{ 'tristate--active': filter_ritual === true, 'tristate--inactive': filter_ritual === false }"
                        title="Ritual"
                    ><RitualIcon /></span>

                    <span
                        class="tristate"
                        @click="cycleConcentration"
                        @contextmenu.prevent="previousConcentration"
                        :class="{ 'tristate--active': filter_concentration === true, 'tristate--inactive': filter_concentration === false }"
                        title="Concentración"
                    ><ConcentrationIcon /></span>
                </div>

                <div class="school_filter_group">
                    <div class="school_filter">
                        <div class="dropdown_btn" @click="school_open = !school_open">Schools <i>-</i></div>
                        <div v-if="school_open" class="dropdown_menu">
                            <div
                                v-for="(name, id) in Schools"
                                :key="id"
                                class="dropdown_option"
                                :class="{ selected: filter_school.includes(+id) }"
                                @click="toggleSchool(+id)"
                            >{{ name }}</div>
                        </div>
                    </div>
                    <div v-if="filter_school.length" class="school_chips">
                        <span v-for="id in filter_school" :key="id" class="school_chip">
                            {{ Schools[id] }}
                            <span class="school_chip_remove" @click.stop="toggleSchool(id)">✕</span>
                        </span>
                    </div>
                </div>

                <button class="hide_show_filters_btn" @click="minMaxFilters()">{{ filters_open ? '▲' : '▼' }}</button>
            </div>

            <div class="spell_tabs">
                <button
                    :class="{ active: spell_tab === 'cantrip' }"
                    @click="spell_tab = 'cantrip'"
                    :disabled="!canUseCantrips"
                >
                    Trucos ({{ selected_cantrips.length }}/{{ cantripLimit }})
                </button>
                <button
                    :class="{ active: spell_tab === 'spell' }"
                    @click="spell_tab = 'spell'"
                >
                    Conjuros ({{ selected_leveled_spells.length }}/{{ preparedSpellsLimit }})
                </button>
            </div>

            <div class="spell_list">
                <div
                    v-for="spell in filteredSpells"
                    :key="spell.id"
                    class="spell_card"
                    :class="{ 'spell_card--expanded': expanded_id === spell.id, 'spell_card--selected': isSelected(spell) }"
                >
                    <!-- 👇 Cabecera: fila con checkbox + base. Es el único bloque en row;
                         .spell_card sigue en column como en el compendio, así
                         .spell_card_expanded (más abajo) no compite por ancho con esto. -->
                    <div class="spell_card_header">
                        <label class="spell_select" @click.stop>
                            <input
                                type="checkbox"
                                :checked="isSelected(spell)"
                                :disabled="!isSelected(spell) && !canSelect(spell)"
                                @change="toggleSpell(spell)"
                            />
                        </label>

                        <div class="spell_card_base" @click="expandSpell(spell)">
                            <span class="spell_name">{{ spell.name }}</span>

                            <span v-if="spell.attackRoll" class="spell_attackroll">A</span>
                            <span v-if="spell.savingThrowAbility" class="spell_savingthrow">S</span>

                            <span class="spell_component_v">{{ spell.components.includes('V') ? 'V' : '' }}</span>
                            <span class="spell_component_s">{{ spell.components.includes('S') ? 'S' : '' }}</span>
                            <span class="spell_component_m">{{ spell.components.includes('M') ? 'M' : '' }}</span>

                            <span class="spell_level">{{ spell.level === 0 ? 'Cantrip' : `Lvl. ${spell.level}` }}</span>
                            <span class="spell_school">{{ spell.school }}</span>

                            <span v-if="spell.ritual" class="spell_ritual">R</span>
                            <span v-if="spell.concentration" class="spell_concentration">C</span>
                        </div>
                    </div>

                    <div v-if="expanded_id === spell.id" class="spell_card_expanded" @click.stop>
                        <div v-if="expanded_loading">Loading...</div>
                        <div v-else class="spell_card_expanded_content">
                            <div class="spell_details">
                                <span>{{ expanded_spell.castingTime }}</span>
                                <span>{{ expanded_spell.range }}</span>
                                <span>{{ expanded_spell.duration }}</span>
                                <span v-if="expanded_spell.material">{{ expanded_spell.material }}</span>
                            </div>
                            <p class="spell_desc" v-html="renderDescription(expanded_spell.description)"></p>

                            <div v-if="expanded_spell.upcasts.length && expanded_spell.upcasts[0].upcastType === 'SLOT'" class="upcast_section">
                                <div class="upcast_levels">
                                    <span
                                        v-for="lvl in expanded_spell.upcasts.map(u => u.level)"
                                        :key="lvl"
                                        class="upcast_button"
                                        :class="{ 'upcast_button--active': selected_upcast_level === lvl }"
                                        @click="selectUpcastLevel(lvl)"
                                    >{{ lvl }}</span>
                                </div>
                                <div class="upcast_result">
                                    {{ getUpcastText(expanded_spell, selected_upcast_level) }}
                                </div>
                            </div>

                            <div v-else-if="expanded_spell.upcasts.length && expanded_spell.upcasts[0].upcastType === 'CANTRIP'" class="upcast_section">
                                <div class="upcast_cantrip_row">
                                    <span v-for="u in expanded_spell.upcasts" :key="u.level" class="upcast_cantrip_step">
                                        Char. Lvl {{ u.level }}: {{ u.damageRoll ?? u.description }}
                                    </span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <button class="character_spell_forward" @click="emit('navigate', {page: 'characterFinalize', characterId: props.characterId})">Continue Creation</button>
        <button class="character_spell_back" @click="goBackToCharacters">Volver a mis personajes</button>
    </div>
</template>

<style scoped>
    /* Estilos NUEVOS y aislados a este componente: pestañas + checkbox.
       .spell_card, .spell_card_base y .spell_card_expanded NO se tocan aquí
       para nada relativo a su dirección de flex — heredan tal cual de
       appSpells.css, igual que en el compendio. */

    .spell_tabs {
        display: flex;
        gap: 8px;
        width: 75%;
        margin: 20px 0 10px 0;
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

    /* Cabecera: fila con el checkbox y la base de la tarjeta.
       Ocupa el mismo alto que .spell_card_base ya traía (40px),
       así el total de .spell_card sigue siendo 60px sin expandir,
       igual que en el compendio. */
    .spell_card_header {
        display: flex;
        flex-direction: row;
        align-items: flex-start;
        gap: 10px;
        width: 100%;
        height: 40px;
    }

    /* .spell_card_base ya trae width:100% y position:relative de
       appSpells.css — aquí solo anulamos el ancho fijo para que
       respete el espacio que le deja el checkbox al lado. */
    .spell_card_base {
        width: auto;
        flex: 1;
    }

    .spell_select {
        flex-shrink: 0;
        padding-top: 2px;
        cursor: pointer;
    }

    .spell_card--selected {
        border-color: var(--accent);
    }
</style>