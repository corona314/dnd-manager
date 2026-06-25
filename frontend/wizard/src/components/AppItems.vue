<script setup>
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
        armor:   [{ key: 'armorType',   label: 'All',    options: ['light', 'medium', 'heavy'] }],
        weapons: [{ key: 'weaponRange', label: 'Alcance', options: ['Melee', 'Ranged'] },  { key: 'weaponType',  label: 'Tipo',    options: ['Simple', 'Martial'] }],
        shields: [],
        items:   [{ key: 'itemType',    label: 'All',    options: ['Item','Wondrous','Gear','Vehicle','Tool','Potion','Ammunition'] }],
    }

    const items = ref([])
    const expanded_item = ref(null)
    const expanded_loading = ref(false)
    const expanded_id = ref(null)


    //Filtros generales
    const filter_name = ref('')
    const filter_price = ref([0,500000])
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
                params.append('priceMin', filter_price.value[0])
            }
            if (filter_price.value[1] !== null) {
                params.append('priceMax', filter_price.value[1])
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
        activeTab.value    = key
        current_page.value = 0
        Object.keys(filter_specific).forEach(k => delete filter_specific[k])
        fetchItems(0)
    }
 
    onMounted(() => {fetchItems()})

    function goToPage(page){
        if (page < 0 || page >= total_pages.value) return
        fetchItems(page)
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
        
            <div class="general_filters">
                <input class="name" type="text" placeholder="Buscar objeto..." v-model="filter_name" @keyup.enter="fetchItems(0)"/>
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
                    <span class="price_label">
                        {{ filter_price[0] + 'GP'}}
                        --
                        {{ filter_price[1] + 'GP'}}
                    </span>
                    <Slider class="price_slider" v-model="filter_price" :min="0" :max="500000" :step="100" range @slideend="fetchItems"/>
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
        
        <!-- Lista -->
        <div v-if="loading">Cargando...</div>
        <div v-else class="item_list">
            <div v-for="item in items" :key="item.id" class="item_card" :class="{ 'item_card--expanded': expanded_id === item.id }" @click="expandItem(item)">
                <div class="item_card_base">
                    <strong class="item_name">{{ item.name }}</strong>
                    <span class="item_rarity">{{ item.rarity }}</span>
                    <span class="item_price">{{ item.price/100 }} gp</span>
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
                        
                        <p class="item_desc" v-html="marked(expanded_item.description)"></p>

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
/*Main-General*/ 
    .items_page{
        min-height: 100vh;
        display: flex;
        align-items: center;
        justify-content: center;
        flex-direction: column;
        background-color: rgb(255, 230, 186);
    }

/*Pestañas*/
    .tabs{
        display: flex;
        width: 50%;
        justify-content: space-between;
        align-items: center;
        margin: 30px 0px 30px 0px;
    }
    .tabs button{
        padding: 8px;
        background-color: rgb(220, 220, 220);
    }

/*Filtros*/
    
/*Filtros generales*/
    .tristate {
        display: inline-flex;
        align-items: center;
        justify-content: center;
        width: 24px;
        height: 24px;
        border: 2px solid #666;
        border-radius: 4px;
        cursor: pointer;
        font-weight: bold;
        user-select: none;
        background-color: white;
    }
    .tristate--active {
        background-color: #4ade80;
        border-color: #16a34a;
    }
    .tristate--inactive {
        background-color: #f87171;
        border-color: #dc2626;
    }
    .tristate:hover {
        border-color: #333;
    }

    .general_filters {
        display: flex;
        align-items: center;
        justify-content: space-between;
        gap: 16px;
        width: 55%;
        margin-bottom: 16px;
    }

    .rarity_filter {
        position: relative;
        z-index: 20;
    }
    .rarity_dropdown_btn {
        padding: 4px 10px;
        border: 1px solid #666;
        cursor: pointer;
        background: white;
        border-radius: 4px;
    }
    .rarity_dropdown_menu {
        position: absolute;
        top: 100%;
        background: white;
        border: 1px solid #666;
        padding: 4px;
        border-radius: 4px;
        min-width: 120px;
    }
    .rarity_option {
        padding: 4px 8px;
        cursor: pointer;
        border-radius: 3px;
    }
    .rarity_option:hover { background: #fde68a; }
    .rarity_option.selected { background: #b45309; color: white; }

    .rarity_chips {
        display: flex;
        flex-wrap: wrap;
        gap: 4px;
        margin-top: 4px;
    }
    .rarity_chip {
        display: inline-flex;
        align-items: center;
        gap: 4px;
        border: 1px solid #b45309;
        padding: 2px 6px;
        border-radius: 999px;
        font-size: 0.8rem;
        background: #fde68a;
        cursor: pointer;
    }

/*Filtros especificos*/
    .specific_filters {
        display: flex;
        justify-content: center;
        gap: 12px;
        width: 75%;
        margin-bottom: 16px;
    }
    .specific_filters select {
        padding: 4px 8px;
        border: 1px solid #666;
        border-radius: 4px;
        background: white;
        cursor: pointer;
    }
/*Tarjetas de items*/
    .item_list{
        display: flex;
        flex-direction: column;
        align-items: stretch; 
        justify-content: space-between;
        width: 75%;
    }
    .item_card{
        display: flex;
        flex-direction: column;
        padding: 10px;
        border: solid;
        border-width: 2px;
        margin: 10px;
        justify-content: space-between;
        position: relative; 
        height: 70px; 
        background-color: white;
        cursor: pointer;
    }
    .item_card_base{
        display: flex;
        flex-direction: column;
        justify-content: space-between;
        width: 100%;
        position: relative;
        height: 50px;
        flex-shrink: 0;
    }
    .item_name{
        position: absolute;
        top: 0;
        left: 0;
    }
    .item_rarity{
        position: absolute;
        bottom: 0;
        left: 0;
    }
    .item_price{
        position: absolute;
        top: 0;
        right: 0;
    }
    .item_weight{
        position: absolute;
        top: 0;
        right: 65px;
    }
    .item_attunement{
        position: absolute;
        bottom: 0;
        right: 0;
    }
    .item_magic{
        position: absolute;
        bottom: 0;
        right: 40px;
    }
    /*Item extendido*/
    .item_card {
        max-height: 80px;
        overflow: hidden;
        transition: max-height 0.3s ease;
        height: auto;         
    }
    .item_card--expanded {
        max-height: 2000px;
    }
    .item_card_expanded {
        margin-top: 10px;
        padding-top: 10px;
        border-top: 1px solid rgba(0,0,0,0.2);
    }
    .item_card_expanded_content {
        display: flex;
        flex-direction: column;
        position: relative;
        gap: 8px;
    }
    .item_details {
        display: flex;
        gap: 16px;
        font-weight: bold;
        flex-wrap: wrap;
    }
    .item_desc {
        line-height: 1.5;
        margin-bottom: 8px;
        font-size: 15px;
    }
    .item_properties {
        display: flex;
        gap: 6px;
        flex-wrap: wrap;
    }
    .item_property_chip {
        border: 1px solid gray;
        padding: 2px 8px;
        border-radius: 20px;
        background: lightgray;
        cursor: help;
    }
    .item_mastery {
        font-size: 13px;
        background: rgba(0,0,0,0.05);
        padding: 6px 10px;
        border-radius: 4px;
        line-height: 1.4;
    }
/*Paginación*/
    .item_pages{
        display: flex;
        justify-content: center;
        align-items: center;
    }
</style>