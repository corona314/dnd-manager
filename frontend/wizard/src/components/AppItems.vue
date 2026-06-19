<script setup>
    import { ref, onMounted } from 'vue'
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

    const items        = ref([])

    const current_page = ref(0)
    const total_pages = ref(1)

    //Metodos
    async function fetchItems(page=0) {
        loading.value = true
        try {
            const tab = Tabs.find(t => t.key === activeTab.value)
            const params = new URLSearchParams({ page, size: 20 })
            const res = await fetch(`${API_BASE}${tab.endpoint}?${params}`, {
                headers: { Authorization: `Bearer ${props.token}` }
            })
            const data = await res.json()

            let content = data.content
            if (activeTab.value === 'items') {
                content = content.filter(item =>
                    item.itemType !== 'Armor' &&
                    item.itemType !== 'Weapon' &&
                    item.itemType !== 'Shield'
                )
            }
            items.value = content


            total_pages.value  = data.totalPages
            current_page.value = data.number
        } catch (e) {
            console.error(e)
        } finally {
            loading.value = false
        }
    }

    function switchTab(key) {
        activeTab.value    = key
        current_page.value = 0
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
                :class="{ active: activeTab.value === tab.key }"
                @click="switchTab(tab.key)">
                {{ tab.label }}
            </button>
        </div>
        <!-- Lista -->
        <div v-if="loading">Cargando...</div>
        <div v-else class="cards">
            <div v-for="item in items" :key="item.id" class="card">
                <strong>{{ item.name }}</strong>
                <span>{{ item.rarity }}</span>
                <span>{{ item.price }} gp · {{ item.weight }} lb</span>
                <span v-if="item.magic">✦ Magic</span>
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