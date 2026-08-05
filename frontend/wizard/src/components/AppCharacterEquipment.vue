<script setup>
    //import './styles/appCharacterEquipment.css'
    import { ref, computed, onMounted } from 'vue'
    const props = defineProps({ token: String, characterId: { type: [Number, String], required: true } })
    const emit = defineEmits(['navigate'])
    const API_BASE = 'http://localhost:8080/api'

    //--- Personaje ---
    const character = ref(null)
    const loading_character = ref(false)

    //--- Detalle de clase y trasfondo (traen items/startingMoney) ---
    const class_detail = ref(null)
    const loading_class = ref(false)
    const background_detail = ref(null)
    const loading_background = ref(false)

    //--- Elección A (paquete de items) o B (dinero) por fuente ---
    const class_choice = ref(null)       // 'A' | 'B' | null
    const background_choice = ref(null)  // 'A' | 'B' | null

    //--- Items opcionales seleccionados manualmente (por id) ---
    const selected_optional_ids = ref([])

    //--- Guardado ---
    const saving = ref(false)
    const error = ref('')

    async function fetchCharacter() {
        loading_character.value = true
        try {
            const res = await fetch(`${API_BASE}/characters/${props.characterId}`, {
                headers: { Authorization: `Bearer ${props.token}` }
            })
            character.value = await res.json()

            if (character.value?.classEntity?.id) {
                await fetchClassDetail(character.value.classEntity.id)
            }
            if (character.value?.background?.id) {
                await fetchBackgroundDetail(character.value.background.id)
            }
        } catch (e) {
            console.error(e)
        } finally {
            loading_character.value = false
        }
    }

    async function fetchClassDetail(classId) {
        loading_class.value = true
        try {
            const res = await fetch(`${API_BASE}/classes/${classId}`, {
                headers: { Authorization: `Bearer ${props.token}` }
            })
            class_detail.value = await res.json()
        } catch (e) {
            console.error(e)
        } finally {
            loading_class.value = false
        }
    }

    async function fetchBackgroundDetail(backgroundId) {
        loading_background.value = true
        try {
            const res = await fetch(`${API_BASE}/backgrounds/${backgroundId}`, {
                headers: { Authorization: `Bearer ${props.token}` }
            })
            background_detail.value = await res.json()
        } catch (e) {
            console.error(e)
        } finally {
            loading_background.value = false
        }
    }

    onMounted(() => {
        fetchCharacter()
    })

    //--- Helpers para separar items fijos / opcionales de un optionGroup ---
    function fixedItemsOf(detail, group) {
        return (detail?.items ?? []).filter(i => i.optionGroup === group && !i.optional)
    }

    function optionalItemsOf(detail, group) {
        return (detail?.items ?? []).filter(i => i.optionGroup === group && i.optional)
    }

    function moneyOf(detail, group) {
        return (detail?.startingMoney ?? []).find(m => m.optionGroup === group)?.amount ?? 0
    }

    //--- Vista previa: items fijos según lo elegido ---
    const class_fixed_items = computed(() => {
        if (class_choice.value !== 'A') return []
        return fixedItemsOf(class_detail.value, 'A')
    })

    const class_optional_items = computed(() => {
        if (class_choice.value !== 'A') return []
        return optionalItemsOf(class_detail.value, 'A')
    })

    const background_fixed_items = computed(() => {
        if (background_choice.value !== 'A') return []
        return fixedItemsOf(background_detail.value, 'A')
    })

    const background_optional_items = computed(() => {
        if (background_choice.value !== 'A') return []
        return optionalItemsOf(background_detail.value, 'A')
    })

    const class_money = computed(() => {
        if (class_choice.value === 'A') return moneyOf(class_detail.value, 'A')
       if (class_choice.value === 'B') return moneyOf(class_detail.value, 'B')
       return 0
    })

    const background_money = computed(() => {
        if (background_choice.value === 'A') return moneyOf(background_detail.value, 'A')
        if (background_choice.value === 'B') return moneyOf(background_detail.value, 'B')
        return 0
    })

    const total_money = computed(() => class_money.value + background_money.value)

    function isOptionalSelected(itemId) {
        return selected_optional_ids.value.includes(itemId)
    }

    function toggleOptional(itemId) {
        if (selected_optional_ids.value.includes(itemId)) {
            selected_optional_ids.value = selected_optional_ids.value.filter(id => id !== itemId)
        } else {
            selected_optional_ids.value = [...selected_optional_ids.value, itemId]
        }
    }

    //--- ¿Puede confirmar? Necesita haber elegido A/B para clase y trasfondo (si existen) ---
    const can_confirm = computed(() => {
        if (character.value?.classEntity && !class_choice.value) return false
        if (character.value?.background && !background_choice.value) return false
        return true
    })

    //--- Construir el listado final de items a añadir, sumando cantidades duplicadas ---
    function buildFinalItemsList() {
        const map = new Map() // itemId -> quantity

        function addEntries(entries) {
            entries.forEach(entry => {
                const id = entry.item.id
                const prev = map.get(id) ?? 0
                map.set(id, prev + entry.quantity)
            })
        }

        addEntries(class_fixed_items.value)
        addEntries(background_fixed_items.value)

        // Opcionales seleccionados (cantidad 1 cada uno, buscando en ambas fuentes)
        const allOptional = [...class_optional_items.value, ...background_optional_items.value]
        allOptional.forEach(entry => {
            if (selected_optional_ids.value.includes(entry.item.id)) {
                const prev = map.get(entry.item.id) ?? 0
                map.set(entry.item.id, prev + entry.quantity)
            }
        })

        return [...map.entries()].map(([itemId, quantity]) => ({ itemId, quantity }))
    }

    //--- Guardar dinero inicial ---
    // NOTA: se asume que el campo del personaje se llama "money". Ajustar si el backend usa otro nombre (ej. "gold").
    async function saveMoney(amount) {
        if (amount <= 0) return true
        try {
            const res = await fetch(`${API_BASE}/characters/${props.characterId}`, {
                method: 'PATCH',
                headers: {
                    Authorization: `Bearer ${props.token}`,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ money: amount })
            })
            return res.ok
        } catch (e) {
            console.error(e)
            return false
        }
    }

    //--- Añadir o actualizar un item del personaje ---
    async function upsertItem(itemId, quantity) {
        const existing = (character.value?.items ?? []).find(ci => ci.item.id === itemId)

        if (existing) {
            const res = await fetch(`${API_BASE}/characters/${props.characterId}/items/${itemId}`, {
                method: 'PATCH',
                headers: {
                    Authorization: `Bearer ${props.token}`,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    quantity: existing.quantity + quantity,
                    equipped: existing.equipped,
                    attuned: existing.attuned
                })
            })
            return res.ok
        } else {
            const res = await fetch(`${API_BASE}/characters/${props.characterId}/items/${itemId}`, {
                method: 'POST',
                headers: {
                    Authorization: `Bearer ${props.token}`,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ quantity, equipped: false, attuned: false })
            })
            return res.ok
        }
    }

    async function confirmEquipment() {
        if (!can_confirm.value) {
            error.value = 'Debes elegir equipo o dinero para cada fuente disponible'
            return
        }

        saving.value = true
        error.value = ''
        try {
            // 1) Dinero (si se eligió opción B en alguna fuente)
            const moneyOk = await saveMoney(total_money.value)
            if (!moneyOk) {
                error.value = 'Error al guardar el dinero inicial'
                return
            }

            // 2) Items (si se eligió opción A en alguna fuente, o hay opcionales marcados)
            const finalItems = buildFinalItemsList()
            for (const entry of finalItems) {
                const ok = await upsertItem(entry.itemId, entry.quantity)
                if (!ok) {
                    error.value = 'Error al añadir uno de los items'
                    return
                }
            }

            emit('navigate', { page: 'characterFinalize', characterId: props.characterId })
        } catch (e) {
            console.error(e)
            error.value = 'Error de conexión'
        } finally {
            saving.value = false
        }
    }

    function goBackToCharacters() {
        emit('navigate', 'characters')
    }
</script>

<template>
    <div class="character_equipment_page">
        <div v-if="loading_character">Cargando personaje...</div>

        <div v-else-if="character" class="character_equipment_header">
            <h1>{{ character.name }}</h1>
            <span class="equipment_subtitle">Elige tu equipo inicial</span>
        </div>

        <span v-if="error" class="character_equipment_error">{{ error }}</span>

        <!--Equipo de Clase-->
        <div v-if="loading_class">Cargando equipo de clase...</div>
        <div v-else-if="class_detail" class="equipment_source_card">
            <h2>Equipo de {{ character.classEntity.name }}</h2>

            <div class="equipment_choice_toggle">
                <button :class="{ active: class_choice === 'A' }" @click="class_choice = 'A'">
                    Paquete de equipo
                </button>
                <button :class="{ active: class_choice === 'B' }" @click="class_choice = 'B'">
                    {{ (moneyOf(class_detail, 'B') / 100).toFixed(2) }} po en su lugar
                </button>
            </div>

            <div v-if="class_choice === 'A'" class="equipment_items_preview">
                <div v-for="entry in class_fixed_items" :key="entry.item.id" class="equipment_item_row">
                    {{ entry.quantity }}x {{ entry.item.name }}
                </div>
                <div v-if="moneyOf(class_detail, 'A') > 0" class="equipment_item_row">
                    {{ (moneyOf(class_detail, 'A') / 100).toFixed(2) }} po
                </div>
                <div v-if="class_optional_items.length" class="equipment_optional_list">
                    <span class="equipment_optional_label">Opcionales:</span>
                    <label v-for="entry in class_optional_items" :key="entry.item.id" class="equipment_optional_row">
                        <input type="checkbox" :checked="isOptionalSelected(entry.item.id)" @change="toggleOptional(entry.item.id)" />
                        {{ entry.quantity }}x {{ entry.item.name }}
                    </label>
                </div>
            </div>

            <div v-else-if="class_choice === 'B'" class="equipment_money_preview">
                Recibirás {{ (moneyOf(class_detail, 'B') / 100).toFixed(2) }} piezas de oro.
            </div>
        </div>

        <!--Equipo de Trasfondo-->
        <div v-if="loading_background">Cargando equipo de trasfondo...</div>
        <div v-else-if="background_detail" class="equipment_source_card">
            <h2>Equipo de {{ character.background.name }}</h2>

            <div class="equipment_choice_toggle">
                <button :class="{ active: background_choice === 'A' }" @click="background_choice = 'A'">
                    Paquete de equipo
                </button>
                <button :class="{ active: background_choice === 'B' }" @click="background_choice = 'B'">
                    {{ (moneyOf(background_detail, 'B') / 100).toFixed(2) }} po en su lugar
                </button>
            </div>

            <div v-if="background_choice === 'A'" class="equipment_items_preview">
                <div v-for="entry in background_fixed_items" :key="entry.item.id" class="equipment_item_row">
                    {{ entry.quantity }}x {{ entry.item.name }}
                </div>
                <div v-if="moneyOf(background_detail, 'A') > 0" class="equipment_item_row">
                    {{ (moneyOf(background_detail, 'A') / 100).toFixed(2) }} po
                </div>
                <div v-if="background_optional_items.length" class="equipment_optional_list">
                    <span class="equipment_optional_label">Opcionales:</span>
                    <label v-for="entry in background_optional_items" :key="entry.item.id" class="equipment_optional_row">
                        <input type="checkbox" :checked="isOptionalSelected(entry.item.id)" @change="toggleOptional(entry.item.id)" />
                        {{ entry.quantity }}x {{ entry.item.name }}
                    </label>
                </div>
            </div>

            <div v-else-if="background_choice === 'B'" class="equipment_money_preview">
                Recibirás {{ (moneyOf(background_detail, 'B') / 100).toFixed(2) }} piezas de oro.
            </div>
        </div>

        <!--Resumen-->
        <div v-if="total_money > 0" class="equipment_total_money">
            Dinero total: {{ (total_money / 100).toFixed(2) }} po
        </div>

        <button class="character_equipment_confirm_btn" @click="confirmEquipment" :disabled="saving || !can_confirm">
            {{ saving ? 'Guardando...' : 'Confirmar equipo' }}
        </button>
        <button class="character_equipment_forward" @click="emit('navigate', {page: 'characterFinalize', characterId: props.characterId})">Continue Creation</button>
        <button class="character_equipment_back" @click="goBackToCharacters">Volver a mis personajes</button>
    </div>
</template>

<style>

</style>