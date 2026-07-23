<script setup>
    import './styles/appCharacterAbilities.css'
    import { ref, computed, onMounted } from 'vue'
    const props = defineProps({ token: String, characterId: { type: [Number, String], required: true } })
    const emit = defineEmits(['navigate'])
    const API_BASE = 'http://localhost:8080/api'

    //Definición fija de las 6 abilities 
    const ABILITY_DEFS = [
        { code: 'STR', label: 'Strength' },
        { code: 'DEX', label: 'Dexterity' },
        { code: 'CON', label: 'Constitution' },
        { code: 'INT', label: 'Inteligence' },
        { code: 'WIS', label: 'Wisdom' },
        { code: 'CHA', label: 'Charisma' },
    ]

    // Coste Point Buy (27 puntos totales)
    const POINT_COSTS = { 8: 0, 9: 1, 10: 2, 11: 3, 12: 4, 13: 5, 14: 7, 15: 9 }
    const TOTAL_POINTS = 27

    //Constantes del personaje
    const character = ref(null)
    const loading_character = ref(false)

    //Constantes de background (para los bonus)
    const background_detail = ref(null)
    const loading_background = ref(false)

    //Point buy: valor base (8-15) por ability, antes de bonus de trasfondo
    const base_scores = ref({ STR: 8, DEX: 8, CON: 8, INT: 8, WIS: 8, CHA: 8 })

    // Reparto de bonus de trasfondo elegido por el usuario: { STR: 2, DEX: 1 } por ejemplo
    const background_bonus_assignment = ref({})

    //Guardado
    const saving = ref(false)
    const error = ref('')

    //--- Fetch personaje ---
    async function fetchCharacter() {
        loading_character.value = true
        try {
            const res = await fetch(`${API_BASE}/characters/${props.characterId}`, {
                headers: { Authorization: `Bearer ${props.token}` }
            })
            character.value = await res.json()

            // Si ya tiene abilities guardadas, precargamos como punto de partida
            if (character.value?.abilities?.length) {
                const map = {}
                character.value.abilities.forEach(a => { map[a.ability] = a.baseValue })
                base_scores.value = { ...base_scores.value, ...map }
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

    //--- Fetch trasfondo (para bonus de abilities) ---
    async function fetchBackgroundDetail(backgroundId) {
        loading_background.value = true
        try {
            const res = await fetch(`${API_BASE}/backgrounds/${backgroundId}`, {
                headers: { Authorization: `Bearer ${props.token}` }
            })
            background_detail.value = await res.json()

            // El campo real es `abilities`: array de códigos, ej. ["INT", "WIS", "CHA"]
        } catch (e) {
            console.error(e)
        } finally {
            loading_background.value = false
        }
    }

    //--- Point Buy: coste actual gastado ---
    const points_spent = computed(() => {
        return Object.values(base_scores.value).reduce((sum, val) => sum + (POINT_COSTS[val] ?? 0), 0)
    })
    const points_remaining = computed(() => TOTAL_POINTS - points_spent.value)

    function increaseScore(code) {
        const current = base_scores.value[code]
        if (current >= 15) return
        const nextCost = POINT_COSTS[current + 1] - POINT_COSTS[current]
        if (points_remaining.value - nextCost < 0) return
        base_scores.value[code] = current + 1
    }

    function decreaseScore(code) {
        const current = base_scores.value[code]
        if (current <= 8) return
        base_scores.value[code] = current - 1
    }

    //--- Bonus de trasfondo (opciones elegibles) ---
    const background_options = computed(() => background_detail.value?.abilities ?? [])

    // Modo de reparto: '2-1' (uno recibe +2, otro +1) o '1-1-1' (los 3 reciben +1)
    const bonus_mode = ref('2-1')

    function setBonusMode(mode) {
        bonus_mode.value = mode
        if (mode === '1-1-1') {
            const map = {}
            background_options.value.forEach(c => { map[c] = 1 })
            background_bonus_assignment.value = map
        } else {
            background_bonus_assignment.value = {}
        }
    }

    function assignBonus(code, amount) {
        if (bonus_mode.value === '1-1-1') {
            // en este modo, los 3 elegibles reciben +1 automáticamente
            background_options.value.forEach(c => { background_bonus_assignment.value[c] = 1 })
            return
        }
        // modo 2-1: un ability recibe +2, otro +1, el resto 0
        const current = { ...background_bonus_assignment.value }
        // limpiar si ya estaba asignado ese valor a otro
        Object.keys(current).forEach(k => { if (current[k] === amount) delete current[k] })
        current[code] = amount
        background_bonus_assignment.value = current
    }

    function bonusFor(code) {
        return background_bonus_assignment.value[code] ?? 0
    }

    const bonus_is_valid = computed(() => {
        if (bonus_mode.value === '1-1-1') {
            return background_options.value.every(c => background_bonus_assignment.value[c] === 1)
        }
        const values = Object.values(background_bonus_assignment.value)
        return values.includes(2) && values.includes(1)
    })

    //--- Cálculo final ---
    function finalScore(code) {
        return base_scores.value[code] + bonusFor(code)
    }

    function modifier(code) {
        return Math.floor((finalScore(code) - 10) / 2)
    }

    function formatModifier(mod) {
        return mod >= 0 ? `+${mod}` : `${mod}`
    }

    //--- Guardar ---
    async function saveAbilities() {
        if (points_remaining.value !== 0) {
            error.value = 'Debes gastar exactamente los 27 puntos de Point Buy'
            return
        }
        if (background_options.value.length && !bonus_is_valid.value) {
            error.value = 'Falta repartir el bonus de trasfondo'
            return
        }

        saving.value = true
        error.value = ''
        try {
            const payload = ABILITY_DEFS.map(a => ({
                ability: a.code,
                baseValue: finalScore(a.code)
            }))

            const res = await fetch(`${API_BASE}/characters/${props.characterId}/abilities`, {
                method: 'PATCH',
                headers: {
                    Authorization: `Bearer ${props.token}`,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(payload)
            })
            if (!res.ok) {
                error.value = 'Error al guardar las características'
                return
            }
            await fetchCharacter()
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

    onMounted(() => {
        fetchCharacter()
    })
</script>

<template>
    <div class="character_ability_page">
        <div v-if="loading_character">Cargando personaje...</div>
        <div v-else-if="character" class="character_ability_header">
            <h1>{{ character.name }}</h1>
        </div>

        <span v-if="error" class="character_ability_error">{{ error }}</span>

        <h2>Point Buy</h2>
        <span class="ability_points_remaining" :class="{ invalid: points_remaining !== 0 }">
            Puntos restantes: {{ points_remaining }} / {{ TOTAL_POINTS }}
        </span>

        <div class="ability_grid">
            <div v-for="ability in ABILITY_DEFS" :key="ability.code" class="ability_card">
                <span class="ability_label">{{ ability.label }} ({{ ability.code }})</span>

                <div class="ability_score_controls">
                    <button @click="decreaseScore(ability.code)" :disabled="base_scores[ability.code] <= 8">-</button>
                    <span class="ability_base_score">{{ base_scores[ability.code] }}</span>
                    <button @click="increaseScore(ability.code)" :disabled="base_scores[ability.code] >= 15">+</button>
                </div>

                <span v-if="bonusFor(ability.code) > 0" class="ability_bg_bonus">
                    + {{ bonusFor(ability.code) }} (trasfondo)
                </span>

                <span class="ability_final_score">
                    Total: {{ finalScore(ability.code) }}
                </span>
                <span class="ability_modifier">
                    Modificador: {{ formatModifier(modifier(ability.code)) }}
                </span>
            </div>
        </div>

        <div v-if="background_options.length" class="background_bonus_section">
            <h2>Bonus de Trasfondo</h2>
            <div class="bonus_mode_selector">
                <button :class="{ active: bonus_mode === '2-1' }" @click="setBonusMode('2-1')">+2 / +1</button>
                <button :class="{ active: bonus_mode === '1-1-1' }" @click="setBonusMode('1-1-1')">+1 / +1 / +1</button>
            </div>

            <div class="bonus_options">
                <div v-for="code in background_options" :key="code" class="bonus_option">
                    <span>{{ code }}</span>
                    <template v-if="bonus_mode === '2-1'">
                        <button :class="{ selected: bonusFor(code) === 2 }" @click="assignBonus(code, 2)">+2</button>
                        <button :class="{ selected: bonusFor(code) === 1 }" @click="assignBonus(code, 1)">+1</button>
                    </template>
                    <template v-else>
                        <span class="bonus_fixed">+1</span>
                    </template>
                </div>
            </div>
        </div>

        <button class="character_ability_save_btn" @click="saveAbilities" :disabled="saving">
            {{ saving ? 'Guardando...' : 'Guardar características' }}
        </button>

        <button class="character_ability_forward" @click="emit('navigate', {page: 'characterFinalize', characterId: props.characterId})">Continue Creation</button>
        <button class="character_ability_back" @click="goBackToCharacters">Volver a mis personajes</button>
    </div>
</template>

<style>

</style>