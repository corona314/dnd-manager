<script setup>
    //Constantes de datos && cosas de que funcione y tal
    import './styles/appSpells.css'
    import Slider from 'primevue/slider';
    import { ref, onMounted, computed } from 'vue'
    import { marked } from 'marked';
    import AttackIcon from './icons/AttackIcon.vue';
    import DefenseIcon from './icons/DefenseIcon.vue';
    import CastingTimeIcon from './icons/CastingTimeIcon.vue';
    import RangeIcon from './icons/RangeIcon.vue';
    import DurationIcon from './icons/DurationIcon.vue';
    import MaterialIcon from './icons/MaterialIcon.vue';


    const props = defineProps({ token: String })
    const API_BASE = 'http://localhost:8080/api'

    const Levels = [0, 1, 2, 3, 4, 5, 6, 7, 8, 9]
    const Components = ['V', 'S', 'M']
    const Schools = {1: 'Abjuration', 2: 'Conjuration', 3: 'Divination', 4: 'Enchantment', 5: 'Evocation', 6: 'Illusion', 7: 'Necromancy',  8: 'Transmutation'}
    const Damage_Colors = {Acid: '--damage-acid', Cold: '--damage-cold', Fire: '--damage-fire', Force: '--damage-force', Lightning: '--damage-lightning', Necrotic: '--damage-necrotic', Piercing: '--damage-piercing', Poison: '--damage-poison', Psychic: '--damage-psychic', Radiant: '--damage-radiant', Slashing: '--damage-slashing', Thunder: '--damage-thunder', Bludgeoning: '--damage-bludgeoning'}

    //Constantes de los filtros
    const filter_name = ref('')
    const filter_level = ref([0,9])
    const prev_level = ref([0, 9])
    const filter_school = ref([])
    const filter_components = ref({V: null, S: null, M: null})
    const filter_ritual = ref(null)
    const filter_concentration = ref(null)
    const school_open = ref(false)
    
    //Constantes de ordenacion
    const sort_labels = {
        name: 'Name',
        level: 'Level',
        school: 'School'
    }

    const sort_field = ref('name')
    const sort_direction = ref('asc')
    const sort_open = ref(false)

    function setSortField(field) {
        sort_field.value = field
        sort_open.value = false
        applyFilters()
    }

    function toggleSortDirection() {
        sort_direction.value =
            sort_direction.value === 'asc' ? 'desc' : 'asc'

        applyFilters()
    }    
    
    //Constantes de la lista de conjuros
    const spells = ref([])
    const loading = ref(false)
    const expanded_spell = ref(null)
    const expanded_loading = ref(false)
    const expanded_id = ref(null)
    const selected_upcast_level = ref(null)

    //Constantes de la paginación
    const current_page = ref(0)
    const total_pages = ref(1)


    const Damage_Text = {
        Fire: '--text-on-dark',
        Cold: '--text-on-dark',
        Lightning: '--text-on-light',
        Poison: '--text-on-dark',
        Acid: '--text-on-light',
        Necrotic: '--text-on-dark',
        Radiant: '--text-on-light',
        Psychic: '--text-on-dark',
        Force: '--text-on-dark',
        Thunder: '--text-on-dark',
        Piercing: '--text-on-dark',
        Slashing: '--text-on-dark',
        Bludgeoning: '--text-on-dark'
    }

    //Metodos
    async function fetchSpells(page = 0) {
        loading.value = true
        try {
            const params = new URLSearchParams({ page, size: 20 })
            
            params.append(
                'sort',
                `${sort_field.value},${sort_direction.value}`
            )

            if (sort_field.value === 'level') {
                params.append('sort', 'name,asc')
            }

            if (sort_field.value === 'school') {
                params.append('sort', 'level,asc')
                params.append('sort', 'name,asc')
            }

            if (filter_name.value) {
                params.append('name', filter_name.value)
            }

            if (filter_level.value[0] !== null) {
                params.append('levelMin', filter_level.value[0])
            }

            if (filter_level.value[1] !== null) {
                params.append('levelMax', filter_level.value[1])
            }

            for (const schoolId of filter_school.value) {
                params.append('schoolId', schoolId)
            }

            for (const component in filter_components.value) {
                const val = filter_components.value[component]

                if (val === true) {
                    params.append('components', component)
                }

                if (val === false) {
                    params.append('components', `!${component}`)
                }
            }

            if (filter_ritual.value === true) {
                params.append('ritual', '1')
            }

            if (filter_ritual.value === false) {
                params.append('ritual', '0')
            }

            if (filter_concentration.value === true) {
                params.append('concentration', '1')
            }

            if (filter_concentration.value === false) {
                params.append('concentration', '0')
            }
            const res = await fetch(`${API_BASE}/spells?${params}`, {
            headers: { Authorization: `Bearer ${props.token}` }
            })
            const data = await res.json()

            spells.value = data.content      
            total_pages.value = data.totalPages
            current_page.value = data.number 

        }catch (e){
            console.error(e)
        }finally{
            loading.value = false
        }
    }

    onMounted(() => fetchSpells(0))

    function applyFilters(){
        fetchSpells(0)
    }

    function goToPage(page){
        if (page < 0 || page >= total_pages.value) return
        fetchSpells(page)
    }

    function cycleRitual(){
        if (filter_ritual.value === null) filter_ritual.value = true
        else if (filter_ritual.value === true) filter_ritual.value = false
        else filter_ritual.value = null
        applyFilters()
    }
    function cycleConcentration(){
        if (filter_concentration.value === null) filter_concentration.value = true
        else if (filter_concentration.value === true) filter_concentration.value = false
        else filter_concentration.value = null
        applyFilters()
    }
    function cycleComponent(comp) {
        const val = filter_components.value[comp]
        if (val === null) filter_components.value[comp] = true
        else if (val === true) filter_components.value[comp] = false
        else filter_components.value[comp] = null
        applyFilters()
    }

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
            console.log(JSON.stringify(expanded_spell.value.description))
            expanded_id.value = spell.id
            selected_upcast_level.value = expanded_spell.value.level + 1
        } catch (e) {
            console.error(e)
        } finally {
            expanded_loading.value = false
        }
    }

    function toggleSchool(id) {
        const i = filter_school.value.indexOf(id)
        if (i === -1) filter_school.value.push(id)
        else filter_school.value.splice(i, 1)
        applyFilters()
    }
    //Metodos Ordenación
    function toggleSort(index) {
        const s = sort_fields.value[index]
        if (s.active) {
            s.active = false
            sort_order.value = sort_order.value.filter(f => f !== s.field)
        } else {
            s.active = true
            sort_order.value.push(s.field)
        }
        applyFilters()
    }

    function toggleSortDir(index) {
        const s = sort_fields.value[index]
        s.dir = s.dir === 'asc' ? 'desc' : 'asc'
        sort_order.value = sort_order.value.filter(f => f !== s.field)
        sort_order.value.unshift(s.field)
        if (!s.active) s.active = true
        applyFilters()
    }

    function sortPriority(field) {
        const i = sort_order.value.indexOf(field)
        return i === -1 ? null : i + 1
    }

    function sliderOrder(value){
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
</script>

<template>
    <div class="spell_page">
        <!--Filtros de selección-->
        <div class="filters">
            <div class="name">
                <input class="name_input" type="text" placeholder="Search spell..." v-model="filter_name" @keyup.enter="applyFilters"/>
                <button class="search_button" @click="applyFilters">🔍</button>
            </div>
            <div class="level">
                <span class="level_label">
                    {{ filter_level[0] === 0 ? 'Cantrip (0)' : `${filter_level[0]}` }}
                    --
                    {{ filter_level[1] === 0 ? 'Cantrip (0)' : `${filter_level[1]}` }}
                </span>
                <Slider class="level_slider" v-model="filter_level" :min="0" :max="9" :step="1" range @slideend="applyFilters" @update:modelValue="sliderOrder"/>
            </div>

            <div class="sort_filter">
                <span class="sort_title">Order by:</span>

                <div class="sort_dropdown">
                    <div class="dropdown_btn" @click="sort_open = !sort_open">
                        {{ sort_labels[sort_field] }}
                        <i>-</i>
                    </div>

                    <div v-if="sort_open" class="dropdown_menu">
                        <div
                            v-for="(label, field) in sort_labels"
                            :key="field"
                            class="dropdown_option"
                            :class="{ selected: sort_field === field }"
                            @click="setSortField(field)"
                        >
                            {{ label }}
                        </div>
                    </div>
                </div>

                <button
                    class="sort_direction"
                    @click="toggleSortDirection"
                    :title="sort_direction === 'asc' ? 'Ascendente' : 'Descendente'"
                >
                    {{ sort_direction === 'asc' ? '▲' : '▼' }}
                </button>
            </div>

            <div class="filter_group components_group">
                <span
                    v-for="comp in Components"
                    :key="comp"
                    class="tristate"
                    @click="cycleComponent(comp)"
                    :class="{
                        'tristate--active': filter_components[comp] === true,
                        'tristate--inactive': filter_components[comp] === false
                    }"
                >
                    {{ comp }}
                </span>
            </div>

            <div class="filter_group special_group">
                <span
                    class="tristate"
                    @click="cycleRitual"
                    :class="{
                        'tristate--active': filter_ritual === true,
                        'tristate--inactive': filter_ritual === false
                    }"
                    title="Ritual"
                >
                R
                </span>

                <span
                    class="tristate"
                    @click="cycleConcentration"
                    :class="{
                        'tristate--active': filter_concentration === true,
                        'tristate--inactive': filter_concentration === false
                    }"
                    title="Concentración"
                >
                C
                </span>
            </div>
            
            <div class="school_filter_group">
                <div class="school_filter">
                    <div class="dropdown_btn" @click="school_open = !school_open">
                        Schools <i>-</i>
                    </div>

                    <div v-if="school_open" class="dropdown_menu">
                        <div
                            v-for="(name, id) in Schools"
                            :key="id"
                            class="dropdown_option"
                            :class="{ selected: filter_school.includes(+id) }"
                            @click="toggleSchool(+id)"
                        >
                            {{ name }}
                        </div>
                    </div>
                </div>

                <div v-if="filter_school.length" class="school_chips">
                    <span v-for="id in filter_school" :key="id"class="school_chip">
                        {{ Schools[id] }}
                        <span class="school_chip_remove" @click.stop="toggleSchool(id)">✕</span>
                    </span>
                </div>
            </div>
        </div>

        <!--Tarjetas de los conjuros-->
        <div v-if="loading" class="loading">Cargando...</div>
        <div v-else class="spell_list">
            <div
                v-for="spell in spells"
                :key="spell.id"
                class="spell_card"
                :style="{
                    backgroundColor: `var(${Damage_Colors[spell.damageTypes[0]?.damageType] || '--surface'})`,
                    color: `var(${Damage_Text[spell.damageTypes[0]?.damageType] || '--text'})`
                }"
                :class="{ 'spell_card--expanded': expanded_id === spell.id }"
                v-no-double-select
                @click="expandSpell(spell)"
            >
                <div class="spell_card_base">
                    <span class="spell_name"> {{ spell.name }} </span>
                    <span v-if="spell.attackRoll" class="spell_attackroll">
                        <AttackIcon />
                    </span>
                    <span v-if="spell.savingThrowAbility" class="spell_savingthrow">
                        <DefenseIcon />
                    </span>

                    <span class="spell_component_v">{{ spell.components.includes('V') ? 'V' : '📀' }}</span>
                    <span class="spell_component_s">{{ spell.components.includes('S') ? 'S' : '📉' }}</span>
                    <span class="spell_component_m">{{ spell.components.includes('M') ? 'M' : '🏥' }}</span>

                    <span class="spell_level"> {{ spell.level === 0 ? 'Cantrip' : `Lvl. ${spell.level}` }} </span>

                    <span class="spell_school"> {{ spell.school }} </span>        

                    <span class="spell_ritual" v-if="spell.ritual === true">R</span>
                    <span class="spell_ritual" v-else>🌂</span>

                    <span class="spell_concentration" v-if="spell.concentration === true">C</span>
                    <span class="spell_concentration" v-else>🔨</span>
                </div>
                <div v-if="expanded_id === spell.id" class="spell_card_expanded" @click.stop>
                    <div v-if="expanded_loading">Cargando...</div>
                    <div v-else class="spell_card_expanded_content">
                    <div class="spell_details">
                        <span><CastingTimeIcon class="spell_detail_icon" /> {{ expanded_spell.castingTime }}</span>
                        <span><RangeIcon class="spell_detail_icon" /> {{ expanded_spell.range }}</span>
                        <span><DurationIcon class="spell_detail_icon" /> {{ expanded_spell.duration }}</span>
                        <span v-if="expanded_spell.material">
                            <MaterialIcon class="spell_detail_icon" /> {{ expanded_spell.material }}
                        </span>
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
                                <span class="upcast_cantrip_step">
                                    Char. Lvl {{ Levels[1] ?? 1 }}: {{ expanded_spell.damageRoll }}
                                </span>
                                <span v-for="u in expanded_spell.upcasts" :key="u.level" class="upcast_cantrip_step">
                                   - -> Char. Lvl {{ u.level }}: {{ u.damageRoll ?? u.description }}
                                </span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!--Selector de página-->
        <div class="spell_pages">
            <button @click="goToPage(0)" :disabled="current_page === 0">««</button>
            <button @click="goToPage(current_page - 1)" :disabled="current_page === 0">‹</button>

            <span>Página {{ current_page + 1 }} de {{total_pages}}</span>

            <button @click="goToPage(current_page + 1)" :disabled="current_page >= total_pages - 1">›</button>
            <button @click="goToPage(total_pages - 1)" :disabled="current_page >= total_pages - 1">»»</button>
        </div>
    </div>
</template>

<style>

</style>