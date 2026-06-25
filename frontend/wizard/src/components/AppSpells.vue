<script setup>
    //Constantes de datos && cosas de que funcione y tal
    import Slider from 'primevue/slider';
    import { ref, onMounted, computed } from 'vue'
    import { marked } from 'marked';
    const props = defineProps({ token: String })
    const API_BASE = 'http://localhost:8080/api'
    defineEmits(['back'])

    const Levels = [0, 1, 2, 3, 4, 5, 6, 7, 8, 9]
    const Components = ['V', 'S', 'M']
    const Schools = {1: 'Abjuration', 2: 'Conjuration', 3: 'Divination', 4: 'Enchantment', 5: 'Evocation', 6: 'Illusion', 7: 'Necromancy',  8: 'Transmutation'}
    const Damage_Colors = {Acid: '#a8c94d', Cold: '#7ec8e3', Fire: '#e25822', Force: '#c084fc', Lightning: '#facc15', Necrotic: '#4ade80', Piercing: '#94a3b8', Poison: '#86efac', Psychic: '#f472b6', Radiant: '#fde68a', Slashing: '#f87171', Thunder:'#818cf8', Bludgeoning:'#a78bfa'}

    //Constantes de los filtros
    const filter_name = ref('')
    const filter_level = ref([0,9])
    const filter_school = ref([])
    const filter_components = ref({V: null, S: null, M: null})
    const filter_ritual = ref(null)
    const filter_concentration = ref(null)
    const school_open = ref(false)
    

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

    //Metodos
    async function fetchSpells(page = 0) {
        loading.value = true
        try{
            const params = new URLSearchParams({ page, size: 20 })
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
                if (val === true)  params.append('components', component)
                if (val === false) params.append('components', `!${component}`)
            }
            if(filter_ritual){
                if (filter_ritual.value === true)  params.append('ritual', '1')
                if (filter_ritual.value === false) params.append('ritual', '0')
            }
            if(filter_concentration){
                if (filter_concentration.value === true)  params.append('concentration', '1')
                if (filter_concentration.value === false) params.append('concentration', '0')
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
            console.log('errores no')
            expanded_spell.value = await res.json()
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

    function sliderOrder(value){
        let [min, max] = value
        if (min > max) {
            max = min
        }
        filter_level.value = [min, max]
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
</script>

<template>
    <div class="spell_page">
        <!--Filtros de selección-->
        <div class="filters">
            <input class="name" type="text" placeholder="Buscar conjuro..." v-model="filter_name" @keyup.enter="applyFilters"/>
            <div class="level">
                <span class="level_label">
                    {{ filter_level[0] === 0 ? 'Truco (0)' : `${filter_level[0]}` }}
                    --
                    {{ filter_level[1] === 0 ? 'Truco (0)' : `${filter_level[1]}` }}
                </span>
                <Slider class="level_slider" v-model="filter_level" :min="0" :max="9" :step="1" range @slideend="applyFilters" @update:modelValue="sliderOrder"/>
            </div>
            <div class="school_filter">
                <div class="school_dropdown_btn" @click="school_open = !school_open"> Escuelas <i>-</i> </div>
                <div v-if="school_open" class="school_dropdown_menu">
                    <div v-for="(name, id) in Schools" :key="id" class="school_option" :class="{ selected: filter_school.includes(+id) }" @click="toggleSchool(+id)">
                        {{ name }}
                    </div>
                </div>
                <div class="school_chips">
                    <span v-for="id in filter_school" :key="id" class="school_chip">
                    {{ Schools[id] }}
                    <span @click="toggleSchool(id)">x</span>
                    </span>
                </div>
            </div>
            <div class="components" v-for="comp in Components" :key="comp">
                <span class="tristate" @click="cycleComponent(comp)" :class="{'tristate--active': filter_components[comp] === true, 'tristate--inactive': filter_components[comp] === false}">
                    <span v-if="filter_components[comp] === null">{{ comp }}</span>
                    <span v-else-if="filter_components[comp] === true">{{ comp }}</span>
                    <span v-else>{{ comp }}</span>
                </span>
            </div>
            <div class="ritual" >
                <span class="tristate" @click="cycleRitual" :class="{'tristate--active': filter_ritual === true, 'tristate--inactive': filter_ritual === false}">
                    <span v-if="filter_ritual === null">R</span>
                    <span v-else-if="filter_ritual === true">R</span>
                    <span v-else>R</span>
                </span>
            </div>
            <div class="concentration" >
                <span class="tristate" @click="cycleConcentration" :class="{'tristate--active': filter_concentration === true, 'tristate--inactive': filter_concentration === false}">
                    <span v-if="filter_concentration === null">C</span>
                    <span v-else-if="filter_concentration === true">C</span>
                    <span v-else>C</span>
                </span>
            </div>
        </div>

        <!--Tarjetas de los conjuros-->
        <div v-if="loading" class="loading">Cargando...</div>
        <div v-else class="spell_list">
            <div v-for="spell in spells" :key="spell.name" class="spell_card" :style="{ backgroundColor: Damage_Colors[spell.damageTypes[0]?.damageType] || '#ffffff'}" :class="{ 'spell_card--expanded': expanded_id === spell.id }" @click="expandSpell(spell)">
                <div class="spell_card_base">
                    <span class="spell_name"> {{ spell.name }} </span>
                    <span class="spell_attackroll">{{spell.attackRoll === true ? '⚔️' : ''}}</span>
                    <span class="spell_savingthrow">{{spell.savingThrowAbility === null ? ' ' : '🛡️'}}</span>

                    <span class="spell_component_v">{{ spell.components.includes('V') ? 'V' : '📀' }}</span>
                    <span class="spell_component_s">{{ spell.components.includes('S') ? 'S' : '📉' }}</span>
                    <span class="spell_component_m">{{ spell.components.includes('M') ? 'M' : '🏥' }}</span>

                    <span class="spell_level"> {{ spell.level === 0 ? 'Truco' : `Nv. ${spell.level}` }} </span>

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
                            <span>🕛: {{ expanded_spell.castingTime }}</span>
                            <span>🔛: {{ expanded_spell.range }}</span>
                            <span>⏳: {{ expanded_spell.duration }}</span>
                            <span v-if="expanded_spell.material">📦: {{ expanded_spell.material }}</span>
                        </div>
                        <p class="spell_desc" v-html="marked(expanded_spell.description)"></p>

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
        <!--Volver a Main-->
        <button @click="$emit('back')">Volver</button>
    </div>
</template>

<style>
    /*Main-General*/ 
    .spell_page{
        min-height: 100vh;
        display: flex;
        align-items: center;
        justify-content: center;
        flex-direction: column;
        position: relative;
        background-color: rgb(255, 230, 186);
    }
    /*Cabecera-Filtrado*/ 
    .filters{
        display: flex;
        width: 75%;
        justify-content: space-between;
        align-items: center;
        margin: 30px 0px 30px 0px;
        position: absolute;
        top: 30px;
    }
    .level{
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 6px;
        width: 120px;
    }
    .level_lalbel{
        font-size: 0.75rem;
        font-weight: bold;
        white-space: nowrap;
    }
    .p-slider {
        height: 6px;
        width: 100%;
        --p-slider-track-background: #be9a7f;
    }
    .p-slider-range {
        --p-slider-range-background: #b45309; /* marrón temático D&D */
    }

    .school_filter {
        display: flex;
        flex-direction: column;
        gap: 8px;
        position: relative;        
    }

    .school_dropdown_menu {
        position: absolute;
        top: 100%;
        z-index: 10;
        background: white;
        border: 1px solid #666;
        padding: 5px;
    }

    .school_chips {
        display: flex;
        flex-wrap: wrap;
        gap: 4px;
    }

    .school_chip {
        display: inline-flex;
        align-items: center;
        gap: 4px;
        border: 1px solid #666;
        padding: 2px 6px;
        border-radius: 999px;
    }

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


    /*Muestra de Spells*/ 
    .spell_list{
        display: flex;
        flex-direction: column;
        align-items: space-between;
        justify-content: space-between;
        width: 75%;
        margin-top: 130px;
    }
    .spell_card{
        display: flex;
        flex-direction: column;
        padding: 10px;
        border: solid;
        border-width: 2px;
        margin: 10px;
        justify-content: space-between;
        position: relative; 
        height: 60px; 
        cursor: pointer;
        transition: height 0.2s;
    }
    .spell_card_base{
        display: flex;
        flex-direction: column;
        justify-content: space-between;
        width: 100%;
        position: relative;
        height: 40px;
        flex-shrink: 0;    
    }

    .spell_card--expanded {
        height: auto;
    }
    .spell_card_expanded {
        margin-top: 10px;
        padding-top: 10px;
        border-top: 1px solid rgba(0,0,0,0.2);
    }
    .spell_desc {
        line-height: 1.5;
        margin-bottom: 8px;
        font-size: 15px;
    }
    .spell_details {
        display: flex;
        gap: 16px;
        font-weight: bold;
    }

    /*TOP LEFT*/ 
    .spell_name {
        position: absolute;
        top: 0;
        left: 0;
        font-weight: bold;
    }
    .spell_attackroll{
        position: absolute;
        top: 0;
        left: 200px;
    }
   /* TOP RIGHT */
    .spell_component_v {
        position: absolute;
        top: 0;
        right: 45px;
        font-size: 0.85rem;
    } 
    .spell_component_s {
        position: absolute;
        top: 0;
        right: 25px;
        font-size: 0.85rem;
    }
    .spell_component_m {
        position: absolute;
        top: 0;
        right: 2px;
        font-size: 0.85rem;
    }

    /* BOTTOM LEFT */
    .spell_level {
        position: absolute;
        bottom: 0;
        left: 0;
        font-size: 0.8rem;
    }

    .spell_school {
        position: absolute;
        bottom: 0;
        left: 60px;          /* separado del nivel */
        font-size: 0.8rem;
    }
    .spell_savingthrow{
        position: absolute;
        bottom: 0;
        left: 200px;
    }
    /* BOTTOM RIGHT */
    .spell_ritual {
        position: absolute;
        bottom: 0;
        right: 40px;
        font-size: 0.8rem;
    }

    .spell_concentration {
        position: absolute;
        bottom: 0;
        right: 0;
        font-size: 0.8rem;
    }

    /*Upcast*/
    .upcast_section {
        margin-top: 10px;
        padding-top: 8px;
        border-top: 1px solid rgba(0,0,0,0.15);
    }
    .upcast_levels {
        display: flex;
        gap: 4px;
        margin-bottom: 6px;
        flex-wrap: wrap;
    }
    .upcast_button {
        width: 26px;
        height: 26px;
        display: flex;
        align-items: center;
        justify-content: center;
        border: 2px solid #666;
        border-radius: 50%;
        cursor: pointer;
        font-size: 0.8rem;
        font-weight: bold;
        background: white;
    }
    .upcast_button--active {
        background-color: grey;
        border-color: black;
        color: white;
    }
    .upcast_result {
        font-size: 14px;
        background: rgba(0,0,0,0.05);
        padding: 6px 10px;
        border-radius: 4px;
    }
    .upcast_cantrip_row {
        display: flex;
        gap: 12px;
        flex-wrap: wrap;
        font-size: 13px;
    }
    /*Selector de pagina*/


</style>