<script setup>
    import '@/styles/character/appCharacterAbilities.css'
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

    //Metodos de seleción de puntos 
    const SCORE_METHOD = { POINT_BUY: 'pointbuy', MANUAL: 'manual' }
    const score_method = ref(SCORE_METHOD.POINT_BUY)

    // Coste Point Buy (27 puntos totales)
    const POINT_COSTS = { 8: 0, 9: 1, 10: 2, 11: 3, 12: 4, 13: 5, 14: 7, 15: 9}
    const TOTAL_POINTS = 27

    //Selección manual de puntos
    const MANUAL_MIN = 1
    const MANUAL_MAX = 20

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

            if (character.value?.background?.id) {
                await fetchBackgroundDetail(character.value.background.id)
            }
        } catch (e) {
            console.error(e)
        } finally {
            loading_character.value = false
        }
    }

    function initializeAbilitiesFromCharacter() {
        if (character.value?.abilities?.length) {
            const map = {}
            character.value.abilities.forEach(a => { map[a.ability] = a.baseValue })
            base_scores.value = { ...base_scores.value, ...map }
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

    // Point Buy: coste actual gastado 
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

    //Selección manual
    function setManualScore(code, rawValue) {
        let value = parseInt(rawValue, 10)
        if (isNaN(value)) value = MANUAL_MIN
        value = Math.min(MANUAL_MAX, Math.max(MANUAL_MIN, value))
        base_scores.value[code] = value
    }

    function setScoreMethod(method) {
        if (method === score_method.value) return
        score_method.value = method
        if (method === SCORE_METHOD.POINT_BUY) {
            const reset = {}
            ABILITY_DEFS.forEach(a => { reset[a.code] = 8 })
            base_scores.value = reset
        }
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

    // Si ya hay características guardadas, se bloquea la edición hasta resetear
    const abilities_locked = ref(false)

    function savedValue(code) {
        return character.value?.abilities?.find(a => a.ability === code)?.baseValue ?? 10
    }

    function savedModifier(code) {
        return Math.floor((savedValue(code) - 10) / 2)
    }

    function checkIfAbilitiesAssigned() {
        const abs = character.value?.abilities ?? []
        abilities_locked.value = abs.length > 0 && abs.some(a => a.baseValue !== 8)
    }

    function resetAbilities() {
        const reset = {}
        ABILITY_DEFS.forEach(a => { reset[a.code] = 8 })
        base_scores.value = reset
        background_bonus_assignment.value = {}
        bonus_mode.value = '2-1'
        score_method.value = SCORE_METHOD.POINT_BUY
        error.value = ''
        abilities_locked.value = false
    }

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
        if (score_method.value === SCORE_METHOD.POINT_BUY && points_remaining.value !== 0) {
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
            abilities_locked.value = true
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

    onMounted(async () => {
        await fetchCharacter()
        checkIfAbilitiesAssigned()
    })
</script>

<template>
    <div class="character_ability_page">
        <div v-if="loading_character">Cargando personaje...</div>
        <div v-else-if="character" class="character_ability_header">
            <h1>{{ character.name }}</h1>
        </div>

        <div class="ability_method_selector">
            <button class="general_button" :class="{ active: score_method === 'pointbuy' }" @click="setScoreMethod('pointbuy')">Point Cost</button>
            <button class="general_button" :class="{ active: score_method === 'manual' }" @click="setScoreMethod('manual')">Manual Assignment</button>
        </div>

        <span v-if="error" class="character_ability_error">{{ error }}</span>
        <!--Metodo de selección de puntos, ya sea compra de puntos, asignar-->
        <template v-if="abilities_locked">
            <h2>Abilities already assigned</h2>
            <button class="general_button character_ability_reset" @click="resetAbilities">Reset and Reassign</button>
        </template>
        <template v-else-if="score_method === 'pointbuy'">
            <h2>Point Cost</h2>
            <span class="ability_points_remaining" :class="{ invalid: points_remaining !== 0 }">
                Remaining: {{ points_remaining }} / {{ TOTAL_POINTS }}
            </span>
        </template>
        <template v-else>
            <h2>Manual Assignment</h2>
        </template>
        <div class="ability_grid">
            <div v-for="ability in ABILITY_DEFS" :key="ability.code" class="ability_card">
                <span class="ability_label">{{ ability.label }} ({{ ability.code }})</span>
                <!-- Modo bloqueado: solo lectura -->
                <template v-if="abilities_locked">
                    <span class="ability_final_score">Score: {{ savedValue(ability.code) }}</span>
                    <span class="ability_modifier">Modifier: {{ formatModifier(savedModifier(ability.code)) }}</span>
                </template>

                <!-- Modo edición-->
                <template v-else>  
                    <div v-if="score_method === 'pointbuy'" class="ability_score_controls">
                        <button class="general_button" @click="decreaseScore(ability.code)" :disabled="base_scores[ability.code] <= 8">-</button>
                        <span class="ability_base_score">{{ base_scores[ability.code] }}</span>
                        <button class="general_button" @click="increaseScore(ability.code)" :disabled="base_scores[ability.code] >= 15">+</button>
                    </div>
                
                    <div v-else class="ability_score_controls">
                        <input type="number" class="ability_manual_input" :min="MANUAL_MIN" :max="MANUAL_MAX" :value="base_scores[ability.code]" @change="setManualScore(ability.code, $event.target.value)"/>
                    </div>
                    <span v-if="bonusFor(ability.code) > 0" class="ability_bg_bonus">
                        + {{ bonusFor(ability.code) }} (background)
                    </span>
                    <span class="ability_final_score">
                        Score: {{ finalScore(ability.code) }}
                    </span>
                    <span class="ability_modifier">
                        Modifier: {{ formatModifier(modifier(ability.code)) }}
                    </span>
                </template>
                    
            </div>
        </div>

        <div v-if="background_options.length" class="background_bonus_section">
            <h2>Background Bonus</h2>
            <div class="bonus_mode_selector">
                <button class="general_button" :class="{ active: bonus_mode === '2-1' }" @click="setBonusMode('2-1')">+2 / +1</button>
                <button class="general_button" :class="{ active: bonus_mode === '1-1-1' }" @click="setBonusMode('1-1-1')">+1 / +1 / +1</button>
            </div>

            <div class="bonus_options">
                <div v-for="code in background_options" :key="code" class="bonus_option">
                    <span>{{ code }}</span>
                    <template v-if="bonus_mode === '2-1'">
                        <button class="general_button" :class="{ selected: bonusFor(code) === 2 }" @click="assignBonus(code, 2)">+2</button>
                        <button class="general_button" :class="{ selected: bonusFor(code) === 1 }" @click="assignBonus(code, 1)">+1</button>
                    </template>
                    <template v-else>
                        <span class="bonus_fixed">+1</span>
                    </template>
                </div>
            </div>
        </div>

        <button class="general_button character_ability_save_btn" @click="saveAbilities" :disabled="saving">
            {{ saving ? 'Saving...' : 'Save Abilities' }}
        </button>

        <button class="general_button character_ability_forward" @click="emit('navigate', {page: 'characterEquipment', characterId: props.characterId})">Continue Creation</button>
        <button class="general_button character_ability_back" @click="goBackToCharacters">Go Back to Characters</button>
    </div>
</template>

<style>

</style>