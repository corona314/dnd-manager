<script setup>
    import './styles/appItems.css'
    import Slider from 'primevue/slider';
    import { ref, onMounted, reactive } from 'vue'
    import { marked } from 'marked'
    const props = defineProps({ token: String })
    const API_BASE = 'http://localhost:8080/api'
    defineEmits(['back'])
    //Constantes 
    const loading = ref(false)
    const activeTab = ref('items')  // pestaña activa

    const Tabs = [
        { key: 'armor',   label: 'Armaduras', endpoint: '/items/armor' },
        { key: 'weapons', label: 'Armas',     endpoint: '/items/weapons' },
        { key: 'shields', label: 'Escudos',   endpoint: '/items/shields' },
        { key: 'items',   label: 'Otros',     endpoint: '/items' },
    ]
    const Rarities = ['common', 'uncommon', 'rare', 'very rare',  'legendary']

    const SpecificFilters = {
        armor:   [{ key: 'armorType',   label: 'All Types',    options: ['Light', 'Medium', 'Heavy'] }],
        weapons: [{ key: 'weaponType',     label: 'All Ranges', options: ['Melee', 'Ranged'] }, { key: 'weaponCategory', label: 'Tipo',        options: ['Simple', 'Martial'] }],
        shields: [],
        items:   [{ key: 'itemType',    label: 'All',    options: ['Item','Wondrous','Gear','Vehicle','Tool','Potion','Ammunition'] }],
    }

    const items = ref([])
    const expanded_item = ref(null)
    const expanded_loading = ref(false)
    const expanded_id = ref(null)


    //Filtros generales
    const filter_name = ref('')
    const filter_price = ref([0,40000])
    const filter_rarity = ref([])
    const filter_magic = ref(null)
    const filter_attunement = ref(null)
    const rarity_open = ref(false)

    //Filtros concretos
    const filter_specific = reactive({})

    //paginación
    const current_page = ref(0)
    const total_pages = ref(1)

    //Metodos
    async function fetchItems(page=0) {
        loading.value = true
        try {
            const tab = Tabs.find(t => t.key === activeTab.value)
            const params = new URLSearchParams({ page, size: 20 })

            if(filter_name.value){
                params.append('name', filter_name.value)
            }
            if(filter_magic){
                if (filter_magic.value === true)  params.append('magic', '1')
                if (filter_magic.value === false) params.append('magic', '0')
            }
            if(filter_attunement){
                if (filter_attunement.value === true)  params.append('attunement', '1')
                if (filter_attunement.value === false) params.append('attunement', '0')
            }

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

            const res = await fetch(`${API_BASE}${tab.endpoint}?${params}`, {
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

    async function expandItem(item) {
        if (expanded_id.value === item.id) {
            expanded_item.value = null
            expanded_id.value = null
            return
        }
        expanded_loading.value = true
        try {
            const tab = Tabs.find(t => t.key === activeTab.value)
            const res = await fetch(`${API_BASE}${tab.endpoint}/${item.id}`, {
                headers: { Authorization: `Bearer ${props.token}` }
            })
            expanded_item.value = await res.json()
            expanded_id.value = item.id
            console.log('Holii')

        } catch (e) {
            console.error(e)
        } finally {
            expanded_loading.value = false
            console.log('haces algo?')
        }
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
        Object.keys(filter_specific).forEach(k => delete filter_specific[k])
        SpecificFilters[key]?.forEach(f => { filter_specific[f.key] = '' })
        fetchItems(0)
    }
 
    onMounted(() => {
        SpecificFilters[activeTab.value]?.forEach(f => { filter_specific[f.key] = '' })
        fetchItems()})

    function goToPage(page){
        if (page < 0 || page >= total_pages.value) return
        fetchItems(page)
    }

    function renderDescription(text) {
        if (!text) return ''
        const fixed = text
            .replace(/\|\s*\|/g, '|\n|')           
            .replace(/(Table:[^\n|]+)\|/, '$1\n|')   
        return marked(fixed)
    }

    function onPriceInputChange() {
        if (filter_price.value[0] > filter_price.value[1]) {
            filter_price.value[1] = filter_price.value[0]
        }
        fetchItems(0)
    }

    function onPriceSlide(value) {}

    function formatPrice(cp) {
        if (cp === 0) return '0 cp'
        if (cp < 10) return `${cp} cp`
        if (cp < 100) return `${cp / 10} sp`
        return `${cp / 100} gp`
    }
</script>

<template>
    <div class="items_page">
        <!-- Pestañas -->
        <div class="tabs">
            <button v-for="tab in Tabs" :key="tab.key"
                :class="{ active: activeTab=== tab.key }"
                @click="switchTab(tab.key)">
                {{ tab.label }}
            </button>
        </div>
        <!--Filtros-->
        <div class="all_filters">
            <div class="general_filters">
                <div class="name">
                    <input class="name" type="text" placeholder="Buscar objeto..." v-model="filter_name" @keyup.enter="fetchItems(0)"/>
                    <button class="search_button" @click="fetchItems(0)">🔍</button>
                </div>
                
                <div class="rarity_filter">
                    <div class="rarity_dropdown_btn" @click="rarity_open = !rarity_open">Rareza ▾</div>
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
                        <input type="number" v-model.number="filter_price[1]" @change="onPriceInputChange" :min="filter_price[0]" max="40000"/>
                    </div>
                    <Slider class="price_slider" v-model="filter_price" :min="0" :max="40000" :step="50" range @slideend="fetchItems" @update:modelValue="onPriceSlide"/>
                </div>
                <div class="magic" >
                    <span class="tristate" @click="cycleMagic" :class="{'tristate--active': filter_magic === true, 'tristate--inactive': filter_magic === false}">
                        <span v-if="filter_magic === null">M</span>
                        <span v-else-if="filter_magic === true">M</span>
                        <span v-else>M</span>
                    </span>
                </div>
                <div class="attunement" >
                    <span class="tristate" @click="cycleAttunement" :class="{'tristate--active': filter_attunement === true, 'tristate--inactive': filter_attunement === false}">
                        <span v-if="filter_attunement === null">A</span>
                        <span v-else-if="filter_attunement === true">A</span>
                        <span v-else>A</span>
                    </span>
                </div>
            </div>
            <div class="specific_filters" v-if="SpecificFilters[activeTab]?.length">
                <template v-for="f in SpecificFilters[activeTab]" :key="f.key">
                    <select v-model="filter_specific[f.key]" @change="fetchItems(0)">
                        <option value="">{{ f.label }}</option>
                        <option v-for="opt in f.options" :key="opt" :value="opt">{{ opt }}</option>
                    </select>
                </template>
            </div>
        </div>
        <!-- Lista -->
        <div v-if="loading">Cargando...</div>
        <div v-else class="item_list">
            <div v-for="item in items" :key="item.id" class="item_card" :class="{ 'item_card--expanded': expanded_id === item.id }" @click="expandItem(item)">
                <div class="item_card_base">
                    <strong class="item_name">{{ item.name }}</strong>
                    <span class="item_rarity">{{ item.rarity }}</span>
                    <span class="item_price">{{ formatPrice(item.price) }} </span>
                    <span class="item_weight">{{ item.weight }} lb</span>
                    <span class="item_magic" v-if="item.magic">✦ Magic</span>
                    <span class="item_attunement">{{ item.attunement === true ? '🧙' : '🙅' }}</span>
                </div>
                <div v-if="expanded_id === item.id" class="item_card_expanded" @click.stop>
                    <div v-if="expanded_loading">Cargando...</div>
                    <div v-else class="item_card_expanded_content">
                        <div class="item_details">
                            <!-- Armor -->
                            <template v-if="expanded_item.armorDto">
                                <span>🛡️ AC: {{ expanded_item.armorDto.acBase }}
                                    <span v-if="expanded_item.armorDto.acMax > expanded_item.armorDto.acBase">
                                        (max {{ expanded_item.armorDto.acMax }})
                                    </span>
                                </span>
                                <span>⚖️ Tipo: {{ expanded_item.armorDto.armorType }}</span>
                                <span v-if="expanded_item.armorDto.strMin > 0">💪 STR mín: {{ expanded_item.armorDto.strMin }}</span>
                                <span v-if="expanded_item.armorDto.stealthDis">🤫 Desventaja Sigilo</span>
                            </template>

                            <!-- Weapon -->
                            <template v-else-if="expanded_item.weaponDto">
                                <span>⚔️ {{ expanded_item.weaponDto.weaponCategory }} · {{ expanded_item.weaponDto.weaponType }}</span>
                                <span v-for="d in expanded_item.weaponDto.damages" :key="d.damageType">
                                    🎲 {{ d.damageRoll }} {{ d.damageType }}
                                </span>
                                <span v-if="expanded_item.weaponDto.rangeNormal > 0">
                                    🏹 Rango: {{ expanded_item.weaponDto.rangeNormal }}/{{ expanded_item.weaponDto.rangeLong }}
                                </span>
                            </template>

                            <!-- Generic -->
                            <template v-else>
                                <span>📦 {{ expanded_item.itemType }}</span>
                            </template>
                            <!-- Propiertys -->
                            <div v-if="expanded_item.weaponDto?.properties?.length" class="item_properties">
                                <span v-for="p in expanded_item.weaponDto.properties" :key="p.name" class="item_property_chip" :title="p.description">
                                    {{ p.name }}<span v-if="p.value"> ({{ p.value }})</span>
                                </span>
                            </div>
                        </div>
                        
                        <p class="item_desc" v-html="renderDescription(expanded_item.description)"></p>

                        <!-- Mastery -->
                        <div v-if="expanded_item.weaponDto?.mastery" class="item_mastery">
                            <strong>{{ expanded_item.weaponDto.mastery.name }}:</strong>
                            {{ expanded_item.weaponDto.mastery.description }}
                        </div>

                    </div>
                </div>
            </div>
        </div>

        <!--Selector de página-->
        <div class="item_pages">
            <button @click="goToPage(0)" :disabled="current_page === 0">««</button>
            <button @click="goToPage(current_page - 1)" :disabled="current_page === 0">‹</button>

            <span>Página {{ current_page + 1 }} de {{total_pages}}</span>

            <button @click="goToPage(current_page + 1)" :disabled="current_page >= total_pages - 1">›</button>
            <button @click="goToPage(total_pages - 1)" :disabled="current_page >= total_pages - 1">»»</button>
        </div>
        <!--Volver a Main-->
        <button @click="$emit('back')">Volver</button>
    </div>
</template>

<style>

</style>