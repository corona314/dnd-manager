<script setup>
    //import '@/styles/appCharacterEquipment.css'
    import CharacterStep from '@/vueComponents/shared/CharacterStep.vue'
    import { ref, computed, onMounted } from 'vue'
    import CharacterBackButton from '../../shared/CharacterBackButton.vue'
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

    //--- Registro de lo aplicado por este paso (para poder deshacerlo sin tocar lo previo) ---
    const equipment_record = ref(null) // { previousMoney, appliedMoney, items: [{ itemId, name, quantity }] }
    const equipment_locked = computed(() => equipment_record.value !== null)
    const resetting = ref(false)
    const storage_key = computed(() => `equipment_applied_${props.characterId}`)

    function loadRecord() {
        try {
            const raw = localStorage.getItem(storage_key.value)
            equipment_record.value = raw ? JSON.parse(raw) : null
        } catch (e) {
            equipment_record.value = null
        }
    }

    function persistRecord(record) {
        equipment_record.value = record
        localStorage.setItem(storage_key.value, JSON.stringify(record))
    }

    function clearRecord() {
        equipment_record.value = null
        localStorage.removeItem(storage_key.value)
    }

    async function fetchCharacter() {
        loading_character.value = true
        try {
            const res = await fetch(`${API_BASE}/characters/${props.characterId}`, {
                headers: { Authorization: `Bearer ${props.token}` }
            })
            character.value = await res.json()

            if (character.value?.classes?.[0]?.classEntity?.id) {
                await fetchClassDetail(character.value.classes[0].classEntity.id)
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

    onMounted(async () => {
        await fetchCharacter()
        loadRecord()
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

    function toggleOptional(itemId, sourceOptionalItems) {
        const idsInGroup = sourceOptionalItems.map(entry => entry.item.id)
        selected_optional_ids.value = [
            ...selected_optional_ids.value.filter(id => !idsInGroup.includes(id)),
            itemId
        ]
    }

    //--- ¿Puede confirmar? Necesita haber elegido A/B para clase y trasfondo (si existen) ---
    const can_confirm = computed(() => {
        if (character.value?.classes?.[0]?.classEntity && !class_choice.value) return false
        if (character.value?.background && !background_choice.value) return false
        return true
    })

    //--- Construir el listado final de items a añadir, sumando cantidades duplicadas ---
    function buildFinalItemsList() {
        const map = new Map() // itemId -> quantity

        function add(entry) {
            const id = entry.item.id
            const prev = map.get(id)
            map.set(id, { name: entry.item.name, quantity: (prev?.quantity ?? 0) + entry.quantity })
        }

        class_fixed_items.value.forEach(add)
        background_fixed_items.value.forEach(add)

        // Opcionales seleccionados (cantidad 1 cada uno, buscando en ambas fuentes)
        const allOptional = [...class_optional_items.value, ...background_optional_items.value]
        allOptional.forEach(entry => {
            if (selected_optional_ids.value.includes(entry.item.id)) add(entry)
        })

        return [...map.entries()].map(([itemId, v]) => ({ itemId, name: v.name, quantity: v.quantity }))
    }

    //--- Guardar dinero inicial ---
    // NOTA: se asume que el campo del personaje se llama "money". Ajustar si el backend usa otro nombre (ej. "gold").
    async function saveMoney(amount) {
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
            error.value = 'You must choose A/B for all sources before confirming'
            return
        }

        saving.value = true
        error.value = ''
        try {
            const previousMoney = character.value?.money ?? 0
            const record = { previousMoney, appliedMoney: total_money.value, items: [] }

            // 1) Dinero (si se eligió opción B en alguna fuente)
            if (total_money.value > 0) {
                const moneyOk = await saveMoney(previousMoney + total_money.value)
                if (!moneyOk) {
                    error.value = 'Error saving initial money'
                    return
                }
            }
            persistRecord({ ...record })

            // 2) Items (si se eligió opción A en alguna fuente, o hay opcionales marcados)
            const finalItems = buildFinalItemsList()
            for (const entry of finalItems) {
                const ok = await upsertItem(entry.itemId, entry.quantity)
                if (!ok) {
                    error.value = 'Error saving one of the items'
                    return
                }
                record.items.push({ itemId: entry.itemId, name: entry.name, quantity: entry.quantity })
                persistRecord({ ...record, items: [...record.items] })
            }

            await fetchCharacter()
        } catch (e) {
            console.error(e)
            error.value = 'Connection error'
        } finally {
            saving.value = false
        }
    }

    async function resetEquipment() {
        if (!equipment_record.value) return
        resetting.value = true
        error.value = ''
        try {
            // Datos frescos para conocer las cantidades actuales
            await fetchCharacter()
            const record = equipment_record.value

            for (const applied of record.items) {
                const existing = (character.value?.items ?? []).find(ci => ci.item.id === applied.itemId)
                if (!existing) continue

                const remaining = existing.quantity - applied.quantity
                const url = `${API_BASE}/characters/${props.characterId}/items/${applied.itemId}`
                let res

                if (remaining > 0) {
                    // Tenía unidades previas: solo se resta lo que añadió este paso
                    res = await fetch(url, {
                        method: 'PATCH',
                        headers: {
                            Authorization: `Bearer ${props.token}`,
                            'Content-Type': 'application/json'
                        },
                        body: JSON.stringify({
                            quantity: remaining,
                            equipped: existing.equipped,
                            attuned: existing.attuned
                        })
                    })
                } else {
                    // Solo existía por este paso: se elimina
                    res = await fetch(url, {
                        method: 'DELETE',
                        headers: { Authorization: `Bearer ${props.token}` }
                    })
                }

                if (!res.ok) {
                    error.value = `Error removing ${applied.name}`
                    return
                }
            }

            // Restaurar el dinero previo
            const moneyOk = await saveMoney(record.previousMoney)
            if (!moneyOk) {
                error.value = 'Error restoring money'
                return
            }

            clearRecord()
            class_choice.value = null
            background_choice.value = null
            selected_optional_ids.value = []
            await fetchCharacter()
        } catch (e) {
            console.error(e)
            error.value = 'Connection error'
        } finally {
            resetting.value = false
        }
    }

    // Siguiente paso: hechizos si la clase los usa; si no, directamente la finalización
    function goNext() {
        const isMagical = class_detail.value?.resources?.some(
            resource => resource?.name === 'Cantrips' || resource?.name === 'Prepared Spells'
        ) ?? false

        emit('navigate', {
            page: isMagical ? 'characterSpells' : 'characterFinalize',
            characterId: props.characterId
        })
    }

    function goBack() {
        emit('navigate', 'characterAbilities')
    }
</script>

<template>
    <CharacterStep
        :error="error"
        @back="goBack"
        @continue="goNext"
    >
        <!--Cabecera con info del personaje-->
        <template #header>
            <div v-if="loading_character">Loading character...</div>
            <div v-else-if="character" class="character_equipment_header">
                <h1>{{ character.name }}</h1>
                <span class="header_subtitle">Choose your initial equipment</span>
            </div>
        </template>

        <!--Modo bloqueado: equipo ya asignado-->
        <div v-if="equipment_locked" class="equipment_locked_summary">
            <h2>Equipment already assigned</h2>

            <div v-for="it in equipment_record.items" :key="it.itemId" class="equipment_item_row">
                {{ it.name }}
                <span v-if="it.quantity > 1" class="item_qty">x{{ it.quantity }}</span>
            </div>
            <div v-if="equipment_record.appliedMoney > 0" class="equipment_item_row">
                {{ (equipment_record.appliedMoney / 100).toFixed(2) }} gp
            </div>

            <button class="general_button character_equipment_reset" @click="resetEquipment" :disabled="resetting">
                {{ resetting ? 'Resetting...' : 'Reset and Reassign' }}
            </button>
        </div>

        <template v-if="!equipment_locked">
            <!--Equipo de Clase-->
            <div v-if="loading_class">Loading class equipment...</div>
            <div v-else-if="class_detail" class="equipment_source_card">
                <h2>{{ character.classes[0].classEntity.name }} Equipment</h2>

                <div class="equipment_choice_toggle">
                    <button class="general_button" :class="{ active: class_choice === 'A' }" @click="class_choice = 'A'">
                        Equipment Package
                    </button>
                    <button class="general_button" :class="{ active: class_choice === 'B' }" @click="class_choice = 'B'">
                        {{ (moneyOf(class_detail, 'B') / 100).toFixed(2) }} gold pieces instead
                    </button>
                </div>

                <div v-if="class_choice === 'A'" class="equipment_items_preview">
                    <div v-for="entry in class_fixed_items" :key="entry.item.id" class="equipment_item_row">
                        {{ entry.item.name }}
                        <span v-if="entry.quantity > 1" class="item_qty">x{{ entry.quantity }}</span>
                    </div>
                    <div v-if="moneyOf(class_detail, 'A') > 0" class="equipment_item_row">
                        {{ (moneyOf(class_detail, 'A') / 100).toFixed(2) }} gp
                    </div>
                    <div v-if="class_optional_items.length" class="equipment_optional_list">
                        <span class="equipment_optional_label">Choose 1:</span>
                        <label v-for="entry in class_optional_items" :key="entry.item.id" class="equipment_optional_row">
                            <input type="radio" name="class_optional_choice" :checked="isOptionalSelected(entry.item.id)" @change="toggleOptional(entry.item.id, class_optional_items)" />
                            {{ entry.item.name }}
                            <span v-if="entry.quantity > 1" class="item_qty">x{{ entry.quantity }}</span>
                        </label>
                    </div>
                </div>

                <div v-else-if="class_choice === 'B'" class="equipment_money_preview">
                    You will receive {{ (moneyOf(class_detail, 'B') / 100).toFixed(2) }} gold pieces.
                </div>
            </div>

            <!--Equipo de Trasfondo-->
            <div v-if="loading_background">Loading background equipment...</div>
            <div v-else-if="background_detail" class="equipment_source_card">
                <h2>{{ character.background.name }} Equipment</h2>

                <div class="equipment_choice_toggle">
                    <button class="general_button" :class="{ active: background_choice === 'A' }" @click="background_choice = 'A'">
                        Equipment Package
                    </button>
                    <button class="general_button" :class="{ active: background_choice === 'B' }" @click="background_choice = 'B'">
                        {{ (moneyOf(background_detail, 'B') / 100).toFixed(2) }} gold pieces instead
                    </button>
                </div>

                <div v-if="background_choice === 'A'" class="equipment_items_preview">
                    <div v-for="entry in background_fixed_items" :key="entry.item.id" class="equipment_item_row">
                        {{ entry.item.name }}
                        <span v-if="entry.quantity > 1" class="item_qty">x{{ entry.quantity }}</span>
                    </div>
                    <div v-if="moneyOf(background_detail, 'A') > 0" class="equipment_item_row">
                        {{ (moneyOf(background_detail, 'A') / 100).toFixed(2) }} gp
                    </div>
                    <div v-if="background_optional_items.length" class="equipment_optional_list">
                        <span class="equipment_optional_label">Choose 1:</span>
                        <label v-for="entry in background_optional_items" :key="entry.item.id" class="equipment_optional_row">
                            <input type="radio" name="background_optional_choice" :checked="isOptionalSelected(entry.item.id)" @change="toggleOptional(entry.item.id, background_optional_items)" />
                            {{ entry.item.name }}
                            <span v-if="entry.quantity > 1" class="item_qty">x{{ entry.quantity }}</span>
                        </label>
                    </div>
                </div>

                <div v-else-if="background_choice === 'B'" class="equipment_money_preview">
                    You will receive {{ (moneyOf(background_detail, 'B') / 100).toFixed(2) }} gold pieces.
                </div>
            </div>

            <!--Resumen-->
            <div v-if="total_money > 0" class="equipment_total_money">
                Total Money: {{ (total_money / 100).toFixed(2) }} gp
            </div>

            <button class="general_button character_equipment_confirm_btn" @click="confirmEquipment" :disabled="saving || !can_confirm">
                {{ saving ? 'Saving...' : 'Confirm Equipment' }}
            </button>
        </template>
    </CharacterStep>
</template>

<style scoped>
    /* ---------- Layout ---------- */

    /* Dentro de CharacterStep los hijos ocupan todo el ancho de la columna */
    h2 {
        margin: 0;
        text-align: center;
        font-size: 1.3rem;
    }

    /* Cabecera en una sola fila y compacta */
    .character_equipment_header {
        display: flex;
        align-items: center;
        justify-content: center;
        flex-wrap: wrap;
        gap: 6px 16px;
    }

    .character_equipment_header h1 {
        margin: 0;
        font-size: 1.5rem;
    }

    /* ---------- Tarjetas (clase, trasfondo y resumen) ---------- */
    .equipment_source_card,
    .equipment_locked_summary {
        display: flex;
        flex-direction: column;
        gap: 12px;
        width: 100%;
        box-sizing: border-box;
        padding: 16px;
        border: 1px solid var(--border);
        border-radius: 10px;
        background: var(--surface);
        margin-bottom: 16px;
        margin-top: 16px;

    }

    /* Elegir entre paquete de equipo o dinero */
    .equipment_choice_toggle {
        display: flex;
        flex-wrap: wrap;
        justify-content: center;
        gap: 8px;
    }

    .equipment_choice_toggle .general_button.active {
        border-color: var(--accent-border);
        background: var(--accent-bg);
        font-weight: 600;
    }

    .general_button:disabled {
        opacity: 0.45;
        cursor: not-allowed;
    }

    /* ---------- Lista de objetos ---------- */
    .equipment_items_preview {
        display: flex;
        flex-direction: column;
        gap: 6px;
    }

    .equipment_item_row {
        display: flex;
        align-items: center;
        justify-content: space-between;
        gap: 12px;
        padding: 6px 10px;
        border: 1px solid var(--separator-lines);
        border-radius: 6px;
    }

    .item_qty {
        margin-left: auto;
        font-size: 0.9em;
        opacity: 0.7;
    }

    /* Objetos entre los que hay que elegir uno */
    .equipment_optional_list {
        display: flex;
        flex-direction: column;
        gap: 4px;
        margin-top: 6px;
        padding-top: 10px;
        border-top: 1px solid var(--separator-lines);
    }

    .equipment_optional_label {
        font-weight: bold;
    }

    .equipment_optional_row {
        display: flex;
        align-items: center;
        gap: 8px;
        padding: 4px 8px;
        border-radius: 6px;
        cursor: pointer;
    }

    .equipment_optional_row:hover {
        background: var(--surface-hover-strong);
    }

    /* ---------- Dinero ---------- */
    .equipment_money_preview {
        padding: 10px;
        border: 1px dashed var(--border);
        border-radius: 8px;
        text-align: center;
        color: var(--text-secondary);
    }

    .equipment_total_money {
        text-align: center;
        font-size: 1.1rem;
        font-weight: 700;
    }

    /* ---------- Botones de acción ---------- */
    .character_equipment_confirm_btn,
    .character_equipment_reset {
        align-self: center;
        padding: 10px 24px;
        font-weight: 700;
    }

    .character_equipment_confirm_btn {
        min-width: 220px;
    }
</style>