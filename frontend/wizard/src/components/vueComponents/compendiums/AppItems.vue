<script setup>
    import '@/styles/appItems.css'
    import Slider from 'primevue/slider';
    import { ref, onMounted, reactive } from 'vue'
    import SearchIcon from '@/icons/SearchIcon.vue';
    import MagicIcon from '@/icons/MagicIcon.vue'
    import NoMagicIcon from '@/icons/NoMagicIcon.vue'
    import AttunementIcon from '@/icons/AttunementIcon.vue'
    import NoAttunementIcon from '@/icons/NoAttunementIcon.vue'
    import CompendiumCard from '@/vueComponents/shared/CompendiumCard.vue'
    import DefenseIcon from '@/icons/DefenseIcon.vue'
    import AttackIcon from '@/icons/AttackIcon.vue'
    import RangeIcon from '@/icons/RangeIcon.vue'
    import DiceIcon from '@/icons/DiceIcon.vue'
    import StrengthIcon from '@/icons/StrengthIcon.vue'
    import StealthIcon from '@/icons/StealthIcon.vue'
    import TagIcon from '@/icons/TagIcon.vue'
    import LayersIcon from '@/icons/LayersIcon.vue'
    import { usingIcons } from '@/composables/usePreferences.js'
    import { useCardExpansion } from '@/composables/useCardExpansion.js'
    import { renderDescription } from '@/composables/renderDescription.js'

    const props = defineProps({ token: String })
    const API_BASE = 'http://localhost:8080/api'
    //Constantes 
    const loading = ref(false)
    const activeTab = ref('items')  // pestaña activa

    const Tabs = [
        { key: 'armor',   label: 'Armor', endpoint: '/items/armor' },
        { key: 'weapons', label: 'Weapons',     endpoint: '/items/weapons' },
        { key: 'shields', label: 'Shields',   endpoint: '/items/shields' },
        { key: 'items',   label: 'Other',     endpoint: '/items' },
    ]
    const Rarities = ['Common', 'Uncommon', 'Rare', 'Very Rare',  'Legendary']

    const SpecificFilters = {
        armor:   [{ key: 'armorType',   label: 'All Types',    options: ['Light', 'Medium', 'Heavy'] }],
        weapons: [{ key: 'weaponType',  label: 'All Ranges', options: ['Melee', 'Ranged'] }, { key: 'category', label: 'Type',        options: ['Simple', 'Martial'] }],
        shields: [],
        items:   [{ key: 'itemType',    label: 'All',    options: ['Item','Wondrous','Gear','Vehicle','Tool','Potion','Ammunition'] }],
    }

    const items = ref([])

    // Expansión de tarjetas: la ruta depende de la pestaña activa
    const currentEndpoint = () => Tabs.find(t => t.key === activeTab.value).endpoint
    const {
        expanded_id,
        expanded: expanded_item,
        expanded_loading,
        expand: expandItem,
        collapse: collapseItem
    } = useCardExpansion(currentEndpoint, () => props.token)

    //Filtros generales
    const filter_name = ref('')
    const filter_price = ref([0,40000])
    const filter_price_max = ref(40000)
    const filter_rarity = ref([])
    const filter_magic = ref(null)
    const filter_attunement = ref(null)
    const rarity_open = ref(false)

    //Filtros concretos
    const filter_specific = reactive({})

    //Ordenación
    const filter_sort_name = ref({ active: false, dir: 'asc' })

    //paginación
    const current_page = ref(0)
    const total_pages = ref(1)

    const filters_open = ref(true)

    function minMaxFilters() {
        filters_open.value = !filters_open.value
    }

    //Metodos
    async function fetchItems(page=0) {
        loading.value = true
        try {
            const params = new URLSearchParams({ page, size: 20 })

            if (filter_sort_name.value.active) {
                params.append('sort', `name,${filter_sort_name.value.dir}`)
            }
            if (filter_name.value) {
                params.append('name', filter_name.value)
            }
            if (filter_magic.value === true)  params.append('magic', '1')
            if (filter_magic.value === false) params.append('magic', '0')

            if (filter_attunement.value === true)  params.append('attunement', '1')
            if (filter_attunement.value === false) params.append('attunement', '0')

            if (filter_price.value[0] !== null) {
                params.append('priceMin', filter_price.value[0]*100)
            }
            if (filter_price.value[1] !== null) {
                params.append('priceMax', filter_price.value[1]*100)
            }

            for (const r of filter_rarity.value) {
                params.append('rarity', r)
            }

            for (const [key, val] of Object.entries(filter_specific)) { 
                if (val) params.append(key, val)
            }

            const res = await fetch(`${API_BASE}${currentEndpoint()}?${params}`, {
                headers: { Authorization: `Bearer ${props.token}` }
            })
            const data = await res.json()

            items.value = data.content
            total_pages.value  = data.totalPages
            current_page.value = data.number
        } catch (e) {
            console.error(e)
        } finally {
            loading.value = false
        }
    }

    function toggleSortNameActive() {
        filter_sort_name.value.active = !filter_sort_name.value.active
        fetchItems(0)
    }

    function toggleSortNameDir() {
        filter_sort_name.value.dir = filter_sort_name.value.dir === 'asc' ? 'desc' : 'asc'
        if (filter_sort_name.value.active) fetchItems(0)
    }

    function cycleMagic(){
        if (filter_magic.value === null) filter_magic.value = true
        else if (filter_magic.value === true) filter_magic.value = false
        else filter_magic.value = null
        fetchItems(0)
    }
    function cycleAttunement(){
        if (filter_attunement.value === null) filter_attunement.value = true
        else if (filter_attunement.value === true) filter_attunement.value = false
        else filter_attunement.value = null
        fetchItems(0)
    }

    function toggleRarity(r) {
        const i = filter_rarity.value.indexOf(r)
        if (i === -1) filter_rarity.value.push(r)
        else filter_rarity.value.splice(i, 1)
        fetchItems(0)
    }
    function switchTab(key) {
        activeTab.value = key
        current_page.value = 0
        collapseItem()
        Object.keys(filter_specific).forEach(k => delete filter_specific[k])
        SpecificFilters[key]?.forEach(f => { filter_specific[f.key] = '' })
        fetchMaxPrice()
        fetchItems(0)
    }
 
    onMounted(() => {
        SpecificFilters[activeTab.value]?.forEach(f => { filter_specific[f.key] = '' })
        fetchMaxPrice()
        fetchItems()
    })

    function goToPage(page){
        if (page < 0 || page >= total_pages.value) return
        fetchItems(page)
    }

    function onPriceInputChange() {
        if (filter_price.value[0] > filter_price.value[1]) {
            filter_price.value[1] = filter_price.value[0]
        }
        fetchItems(0)
    }

    function formatPrice(cp) {
        if (cp === 0) return '0 cp'
        if (cp < 10) return `${cp} cp`
        if (cp < 100) return `${cp / 10} sp`
        return `${cp / 100} gp`
    }

    async function fetchMaxPrice() {
        try {
            const params = new URLSearchParams({ page: 0, size: 1, sort: 'price,desc' })

            const res = await fetch(`${API_BASE}${currentEndpoint()}?${params}`, {
                headers: { Authorization: `Bearer ${props.token}` }
            })
            const data = await res.json()

            if (data.content?.length) {
                filter_price_max.value = data.content[0].price / 100
            } else {
                filter_price_max.value = 40000
            }
            filter_price.value = [0, filter_price_max.value]
        } catch (e) {
            console.error(e)
            filter_price_max.value = 40000
        }
    }
</script>

<template>
    <div class="compendium_page">
        <!-- Pestañas -->
        <div class="tabs">
            <button v-for="tab in Tabs" :key="tab.key"
                :class="{ active: activeTab === tab.key }"
                @click="switchTab(tab.key)">
                {{ tab.label }}
            </button>
        </div>

        <!--Filtros-->
        <div class="compendium_filters" :class="{ 'compendium_filters--closed': !filters_open }">
            <div class="general_filters">
                <div class="name">
                    <input class="name_input" type="text" placeholder="Search item..." v-model="filter_name" @keyup.enter="fetchItems(0)"/>
                    <button class="search_button" @click="fetchItems(0)"><SearchIcon /></button>
                </div>

                <div class="rarity_filter">
                    <div class="rarity_dropdown_btn" @click="rarity_open = !rarity_open">Rarity ▾</div>
                    <div v-if="rarity_open" class="rarity_dropdown_menu">
                        <div v-for="r in Rarities" :key="r" class="rarity_option"
                            :class="{ selected: filter_rarity.includes(r) }"
                            @click="toggleRarity(r)">
                            {{ r }}
                        </div>
                    </div>
                    <div class="rarity_chips">
                        <span v-for="r in filter_rarity" :key="r" class="rarity_chip">
                            {{ r }} <span @click="toggleRarity(r)">x</span>
                        </span>
                    </div>
                </div>

                <div class="price">
                    <div class="price_inputs">
                        <input type="number" v-model.number="filter_price[0]" @change="onPriceInputChange" min="0" :max="filter_price[1]"/>
                        <span>—</span>
                        <input type="number" v-model.number="filter_price[1]" @change="onPriceInputChange" :min="filter_price[0]" :max="filter_price_max"/>
                    </div>
                    <Slider class="price_slider" v-model="filter_price" :min="0" :max="filter_price_max" :step="50" range @slideend="fetchItems(0)"/>
                </div>

                <div class="magic">
                    <span
                        class="tristate tristate_magic"
                        title="Magic"
                        @click="cycleMagic"
                        :class="{ 'tristate--active': filter_magic === true, 'tristate--inactive': filter_magic === false }"
                    >
                        <template v-if="usingIcons">
                            <NoMagicIcon v-if="filter_magic === false" />
                            <MagicIcon v-else />
                        </template>
                        <template v-else>M</template>
                    </span>
                </div>

                <div class="attunement">
                    <span
                        class="tristate tristate_attunement"
                        title="Attunement"
                        @click="cycleAttunement"
                        :class="{ 'tristate--active': filter_attunement === true, 'tristate--inactive': filter_attunement === false }"
                    >
                        <template v-if="usingIcons">
                            <NoAttunementIcon v-if="filter_attunement === false" />
                            <AttunementIcon v-else />
                        </template>
                        <template v-else>A</template>
                    </span>
                </div>

                <div class="sort_filter">
                    <div class="sort_chip" :class="{ 'sort_chip--active': filter_sort_name.active }">
                        <span class="sort_label" @click="toggleSortNameActive">Name</span>
                        <span class="sort_dir_btn" @click="toggleSortNameDir">
                            {{ filter_sort_name.dir === 'asc' ? '▲' : '▼' }}
                        </span>
                    </div>
                </div>
            </div>

            <div class="specific_filters" v-if="SpecificFilters[activeTab]?.length">
                <template v-for="f in SpecificFilters[activeTab]" :key="f.key">
                    <select class="specific_filter" v-model="filter_specific[f.key]" @change="fetchItems(0)">
                        <option value="">{{ f.label }}</option>
                        <option v-for="opt in f.options" :key="opt" :value="opt">{{ opt }}</option>
                    </select>
                </template>
            </div>
            <button class="compendium_filters_toggle" @click="minMaxFilters()">{{ filters_open ? '▲' : '▼' }}</button>
        </div>

        <!-- Lista -->
        <div v-if="loading">Loading...</div>
        <div v-else class="item_list compendium_scroll">
            <CompendiumCard
                v-for="item in items"
                :key="item.id"
                style="--card-base-height: 50px"
                :expanded="expanded_id === item.id"
                @toggle="expandItem(item)"
            >
                <template #base>
                    <strong class="item_name">{{ item.name }}</strong>
                    <span class="item_rarity">{{ item.rarity }}</span>
                    <span class="item_price">{{ formatPrice(item.price) }} </span>
                    <span class="item_weight">{{ item.weight }} lb</span>

                    <span class="item_magic" :class="{ icon_off: !item.magic }" :title="item.magic ? 'Magic' : 'Not magic'">
                        <template v-if="usingIcons">
                            <MagicIcon v-if="item.magic" />
                            <NoMagicIcon v-else />
                        </template>
                        <template v-else>M</template>
                    </span>

                    <span class="item_attunement" :class="{ icon_off: !item.attunement }" :title="item.attunement ? 'Requires attunement' : 'No attunement'">
                        <template v-if="usingIcons">
                            <AttunementIcon v-if="item.attunement" />
                            <NoAttunementIcon v-else />
                        </template>
                        <template v-else>A</template>
                    </span>
                </template>

                <template #expanded>
                    <div v-if="expanded_loading">Loading...</div>
                    <div v-else-if="expanded_item" class="card_expanded_content">
                        <div class="card_details">
                            <!-- Armor -->
                            <template v-if="expanded_item.armorDto">
                                <span><DefenseIcon /> AC: {{ expanded_item.armorDto.acBase }}
                                    <span v-if="expanded_item.armorDto.acMax > expanded_item.armorDto.acBase">
                                        (max {{ expanded_item.armorDto.acMax }})
                                    </span>
                                </span>
                                <span><LayersIcon /> Type: {{ expanded_item.armorDto.armorType }}</span>
                                <span v-if="expanded_item.armorDto.strMin > 0"><StrengthIcon /> Min STR: {{ expanded_item.armorDto.strMin }}</span>
                                <span v-if="expanded_item.armorDto.stealthDis"><StealthIcon /> Stealth Disadvantage</span>
                            </template>

                            <!-- Weapon -->
                            <template v-else-if="expanded_item.weaponDto">
                                <span><AttackIcon /> {{ expanded_item.weaponDto.weaponCategory }} · {{ expanded_item.weaponDto.weaponType }}</span>
                                <span v-for="d in expanded_item.weaponDto.damages" :key="d.damageType">
                                    <DiceIcon /> {{ d.damageRoll }} {{ d.damageType }}
                                </span>
                                <span v-if="expanded_item.weaponDto.rangeNormal > 0">
                                    <RangeIcon /> Range: {{ expanded_item.weaponDto.rangeNormal }}/{{ expanded_item.weaponDto.rangeLong }}
                                </span>
                            </template>

                            <!-- Generic -->
                            <template v-else>
                                <span><TagIcon /> {{ expanded_item.itemType }}</span>
                            </template>

                            <!-- Properties -->
                            <div v-if="expanded_item.weaponDto?.properties?.length" class="item_properties">
                                <span v-for="p in expanded_item.weaponDto.properties" :key="p.name" class="item_property_chip" :title="p.description">
                                    {{ p.name }}<span v-if="p.value"> ({{ p.value }})</span>
                                </span>
                            </div>
                        </div>

                        <p class="card_desc" v-html="renderDescription(expanded_item.description)"></p>

                        <!-- Mastery -->
                        <div v-if="expanded_item.weaponDto?.mastery" class="item_mastery">
                            <strong>{{ expanded_item.weaponDto.mastery.name }}:</strong>
                            {{ expanded_item.weaponDto.mastery.description }}
                        </div>
                    </div>
                </template>
            </CompendiumCard>
        </div>

        <!--Selector de página-->
        <div class="compendium_pages">
            <button class="compendium_page_button page_button--first" @click="goToPage(0)" :disabled="current_page === 0">«</button>
            <button class="compendium_page_button page_button" @click="goToPage(current_page - 1)" :disabled="current_page === 0">‹</button>

            <span class="page_info">Page {{ current_page + 1 }} of {{total_pages}}</span>

            <button class="compendium_page_button page_button" @click="goToPage(current_page + 1)" :disabled="current_page >= total_pages - 1">›</button>
            <button class="compendium_page_button page_button--last" @click="goToPage(total_pages - 1)" :disabled="current_page >= total_pages - 1">»</button>
        </div>
    </div>
</template>

<style>

</style>