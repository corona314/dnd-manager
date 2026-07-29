<script setup>
    //import './styles/appCharacterLevelUp.css'
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

    //--- Cálculo de HP máxima (regla 2024: valor del dado + mod CON) ---
    const hit_die_max = computed(() => {
        const die = character.value?.classEntity?.hitPointDie 
        if (!die) return null
        const match = die.match(/d(\d+)/i)
        return match ? parseInt(match[1], 10) : null
    })

    const con_modifier = computed(() => modifierOf('CON'))

    const calculated_max_hp = computed(() => {
        if (hit_die_max.value === null) return null
        // Mínimo 1 HP, por si el mod de CON fuera muy negativo
        return character.value?.maxHp + Math.max(1, hit_die_max.value + con_modifier.value)
    })

    //--- Cálculo de modificador de característica ---
    function abilityValue(code) {
        return character.value?.abilities?.find(a => a.ability === code)?.baseValue ?? 10
    }

    function modifierOf(code) {
        return Math.floor((abilityValue(code) - 10) / 2)
    }

    //--- Subir de Nivel ---
    async function levelUpCharacter(){
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

            // 2) Finalizamos (el backend sube nivel y marca status)
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
    <div class="character_level_up_page">
        <div v-if="loading_character">Cargando personaje...</div>

        <div v-else-if="character" class="character_level_up_summary">
            <h1>{{ character.name }}</h1>
            <span class="level_up_level">Nivel: {{ character.level }} -> {{ character.level + 1 }}</span>

            <div class="level_up_section">
                <span class="level_up_label">Especie:</span>
                <span>{{ character.species?.name}}</span>
            </div>

            <div class="level_up_section">
                <span class="level_up_label">Clase:</span>
                <span>{{ character.classEntity?.name }} ({{ character.classEntity?.hitPointDie}})</span>
            </div>

            <div class="level_up_section">
                <span class="level_up_label">Trasfondo:</span>
                <span>{{ character.background?.name }}</span>
            </div>

            <div class="level_up_hp_preview">
                <span>Hp:</span>
                <span>{{ character.maxHp }}->{{ calculated_max_hp }}</span>
            </div>
        </div>

        <button class="character_level_up_save_btn" @click="levelUpCharacter" :disabled="saving || calculated_max_hp === null || character.level >= 20">
            {{ saving ? 'Finalizando...' : 'Level Up' }}
        </button>
        <button class="character_level_up_back" @click="goBackToCharacters">Volver a mis personajes</button>
    </div>
</template>

<style>

</style>