<script setup>
    import './styles/appCharacterFinalize.css'
    import { ref, computed, onMounted } from 'vue'
    const props = defineProps({ token: String, characterId: { type: [Number, String], required: true } })
    const emit = defineEmits(['navigate'])
    const API_BASE = 'http://localhost:8080/api'

    const character = ref(null)
    const loading_character = ref(false)

    const saving = ref(false)
    const error = ref('')

    async function fetchCharacter() {
        loading_character.value = true
        try {
            const res = await fetch(`${API_BASE}/characters/${props.characterId}`, {
                headers: { Authorization: `Bearer ${props.token}` }
            })
            character.value = await res.json()
        } catch (e) {
            console.error(e)
        } finally {
            loading_character.value = false
        }
    }

    onMounted(() => {
        fetchCharacter()
    })

    //--- Cálculo de modificador de característica ---
    function abilityValue(code) {
        return character.value?.abilities?.find(a => a.ability === code)?.baseValue ?? 10
    }

    function modifierOf(code) {
        return Math.floor((abilityValue(code) - 10) / 2)
    }

    //--- Cálculo de HP máxima a nivel 1 (regla 2024: valor máximo del dado + mod CON) ---
    const hit_die_max = computed(() => {
        const die = character.value?.classEntity?.hitPointDie // ej: "1d8"
        if (!die) return null
        const match = die.match(/d(\d+)/i)
        return match ? parseInt(match[1], 10) : null
    })

    const con_modifier = computed(() => modifierOf('CON'))

    const calculated_max_hp = computed(() => {
        if (hit_die_max.value === null) return null
        // Mínimo 1 HP, por si el mod de CON fuera muy negativo
        return Math.max(1, hit_die_max.value + con_modifier.value)
    })

    function formatModifier(mod) {
        return mod >= 0 ? `+${mod}` : `${mod}`
    }

    //--- Finalizar ---
    async function finalizeCharacter() {
        if (calculated_max_hp.value === null) {
            error.value = 'No se puede calcular la vida: falta clase asignada'
            return
        }

        saving.value = true
        error.value = ''
        try {
            // 1) Guardamos maxHp/currentHp calculados
            const patchRes = await fetch(`${API_BASE}/characters/${props.characterId}`, {
                method: 'PATCH',
                headers: {
                    Authorization: `Bearer ${props.token}`,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    maxHp: calculated_max_hp.value,
                    currentHp: calculated_max_hp.value
                })
            })
            if (!patchRes.ok) {
                error.value = 'Error al guardar la vida del personaje'
                return
            }

            // 2) Finalizamos (el backend sube nivel 0->1 y marca status)
            const finalizeRes = await fetch(`${API_BASE}/characters/${props.characterId}/finalize`, {
                method: 'PATCH',
                headers: {
                    Authorization: `Bearer ${props.token}`
                }
            })
            if (!finalizeRes.ok) {
                error.value = 'Error al finalizar el personaje'
                return
            }

            // 3) Volvemos al listado de personajes
            emit('navigate', 'characters')
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
    <div class="character_finalize_page">
        <div v-if="loading_character">Cargando personaje...</div>

        <div v-else-if="character" class="character_finalize_summary">
            <h1>{{ character.name }}</h1>
            <span class="finalize_level">Nivel actual: {{ character.level }} → 1</span>

            <div class="finalize_section">
                <span class="finalize_label">Especie:</span>
                <span>{{ character.species?.name ?? 'Sin asignar' }}</span>
            </div>

            <div class="finalize_section">
                <span class="finalize_label">Clase:</span>
                <span>{{ character.classEntity?.name ?? 'Sin asignar' }} ({{ character.classEntity?.hitPointDie ?? '—' }})</span>
            </div>

            <div class="finalize_section">
                <span class="finalize_label">Trasfondo:</span>
                <span>{{ character.background?.name ?? 'Sin asignar' }}</span>
            </div>

            <div class="finalize_abilities">
                <h2>Características</h2>
                <div v-for="ab in character.abilities" :key="ab.ability" class="finalize_ability_row">
                    <span>{{ ab.ability }}</span>
                    <span>{{ ab.baseValue }}</span>
                    <span>({{ formatModifier(modifierOf(ab.ability)) }})</span>
                </div>
            </div>

            <div class="finalize_hp_preview">
                <h2>Vida (calculada)</h2>
                <span v-if="calculated_max_hp !== null">
                    {{ hit_die_max }} (dado) {{ formatModifier(con_modifier) }} (CON) = <strong>{{ calculated_max_hp }} HP</strong>
                </span>
                <span v-else class="finalize_hp_warning">
                    No se puede calcular: falta asignar una clase
                </span>
            </div>
        </div>

        <span v-if="error" class="character_finalize_error">{{ error }}</span>

        <button class="character_finalize_save_btn" @click="finalizeCharacter" :disabled="saving || calculated_max_hp === null">
            {{ saving ? 'Finalizando...' : 'Finalizar y Guardar' }}
        </button>

        <button class="character_finalize_back" @click="goBackToCharacters">Volver a mis personajes</button>
    </div>
</template>

<style>

</style>