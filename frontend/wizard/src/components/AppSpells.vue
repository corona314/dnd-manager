<script setup>
    //Constantes de datos && cosas de que funcione y tal
    import { ref, onMounted } from 'vue'
    const props = defineProps({ token: String })
    const API_BASE = 'http://localhost:8080/api'
    defineEmits(['back'])

    const Levels = [0, 1, 2, 3, 4, 5, 6, 7, 8, 9]
    const Components = ['V', 'S', 'M']
    const Schools = {1: 'Abjuration', 2: 'Conjuration', 3: 'Divination', 4: 'Enchantment', 5: 'Evocation', 6: 'Illusion', 7: 'Necromancy',  8: 'Transmutation'}
    const Damage_Colors = {acid: '#a8c94d', cold: '#7ec8e3', fire: '#e25822', force: '#c084fc', lightning: '#facc15', necrotic: '#4ade80', piercing: '#94a3b8', poison: '#86efac', psychic: '#f472b6', radiant: '#fde68a', slashing: '#f87171', thunder:'#818cf8', bludgeoning:'#a78bfa'}

    //Constantes de los filtros
    const filter_name = ref('')
    const filter_level = ref(null)
    const filter_school = ref(null)
    const filter_components = ref({V: null, S: null, M: null})
    const filter_ritual = ref(null)
    const filter_concentration = ref(null)
    

    //Constantes de la lista de conjuros
    const spells = ref([])
    const loading = ref(false)

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
            if (filter_level.value !== null) {
                params.append('level', filter_level.value)
            }
            if(filter_school.value !== null){
                params.append('schoolId', filter_school.value)
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

</script>

<template>
    <div class="spell_page">
        <!--Filtros de selección-->
        <div class="filters">
            <input class="name" type="text" placeholder="Buscar conjuro..." v-model="filter_name" @input="applyFilters"/>
            <select class="level" v-model="filter_level"  @change="applyFilters">
                <option :value="null">Todos los niveles</option>
                <option v-for="lvl in Levels" :key="lvl" :value="lvl">
                    {{ lvl === 0 ? 'Truco (0)' : `Nivel ${lvl}` }}
                </option>
            </select>
            <select class="school" v-model="filter_school" @change="applyFilters">
                <option :value="null">Todos las escuelas</option>
                <option v-for="(name, id) in Schools" :key="id" :value="id">
                    {{ name }}
                </option>
            </select>
            <div class="components" v-for="comp in Components" :key="comp">
                <p>{{ comp }}</p>
                <select v-model="filter_components[comp]" @change="applyFilters" >
                    <option :value="null">--</option>
                    <option :value="true">Incluir</option>
                    <option :value="false">Excluir</option>
                </select>
            </div>
            <div class="ritual" >
                <p>Ritual</p>
                <select v-model="filter_ritual" @change="applyFilters" >
                    <option :value="null">--</option>
                    <option :value="true">Incluir</option>
                    <option :value="false">Excluir</option>
                </select>
            </div>
            <div class="concentration" >
                <p>Concentracion</p>
                <select v-model="filter_concentration" @change="applyFilters" >
                    <option :value="null">--</option>
                    <option :value="true">Incluir</option>
                    <option :value="false">Excluir</option>
                </select>
            </div>
        </div>

        <!--Tarjetas de los conjuros-->
        <div v-if="loading" class="loading">Cargando...</div>
        <div v-else class="spell_list">
            <div v-for="spell in spells" :key="spell.name" class="spell_card" :style="{ backgroundColor: Damage_Colors[spell.damageTypes[0]?.damageType] || '#ffffff'}">
                <div class="spell_card_base">
                    <div class="spell_card_base_left">
                        <span class="spell_name"> {{ spell.name }} </span>
                        <span class="spell_school"> {{ spell.school }} </span>
                        <span class="spell_level"> {{ spell.level === 0 ? 'Truco' : `Nv. ${spell.level}` }} </span>
                    </div>
                    <div class="spell_card_base_right">
                        <span v-if="spell.ritual === true">Ritual</span>
                        <span v-if="spell.concentration === true">Concentration</span>
                        <span class="spell_components"> {{ spell.components }} </span>
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
        background-color: rgb(255, 230, 186);
    }
    /*Cabecera-Filtrado*/ 
    .filters{
        display: flex;
        width: 75%;
        justify-content: space-between;
        align-items: center;
        margin: 0px 0px 30px 0px;
    }
    /*Muestra de Spells*/ 
    .spell_list{
        display: flex;
        flex-direction: column;
        align-items: space-between;
        justify-content: space-between;
        width: 75%;
    }
    .spell_card{
        display: flex;
        padding: 10px;
        border: solid;
        border-width: 2px;
        margin: 10px;
        justify-content: space-between;
    }
    .spell_card_base{
        display: flex;
        justify-content: space-between;
        width: 100%;
    }
    .spell_card_base_left{
        display: flex;
        justify-content: space-between;
        width: 30%;
    }
    .spell_card_base_right{
        display: flex;
        justify-content: space-between;
        width: 20%;
    }
    /*Selector de pagina*/
</style>