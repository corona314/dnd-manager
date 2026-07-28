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

    //--- Skills de clase ---
    const class_detail = ref(null)
    const loading_class = ref(false)
    const selected_skill_ids = ref([])    
    const pregranted_skill_ids = ref([]) 
    const saving_skills = ref(false)
    const skills_error = ref('')

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

            const eligibleIds = new Set((class_detail.value?.skills ?? []).map(s => s.id))

            const alreadyProficient = (character.value?.skills ?? [])
                .filter(cs => cs.proficient && eligibleIds.has(cs.skill.id))
                .map(cs => cs.skill.id)

            pregranted_skill_ids.value = alreadyProficient
            
            selected_skill_ids.value = alreadyProficient.length ? [] : []
        } catch (e) {
            console.error(e)
        } finally {
            loading_class.value = false
        }
    }

    //--- Selección de skills de clase ---
   const skill_limit = computed(() => class_detail.value?.numberSkills ?? 0)

    function isPregranted(skillId) {
        return pregranted_skill_ids.value.includes(skillId)
    }

    function isSkillSelected(skillId) {
        return isPregranted(skillId) || selected_skill_ids.value.includes(skillId)
    }

    function canToggleOn() {
        return selected_skill_ids.value.length < skill_limit.value
    }

    function toggleSkill(skillId) {
        if (isPregranted(skillId)) return 
        if (selected_skill_ids.value.includes(skillId)) {
            selected_skill_ids.value = selected_skill_ids.value.filter(id => id !== skillId)
        } else if (canToggleOn()) {
            selected_skill_ids.value = [...selected_skill_ids.value, skillId]
        }
    }

    const skills_are_valid = computed(() => selected_skill_ids.value.length === skill_limit.value)

    async function saveClassSkills() {
        if (!skills_are_valid.value) {
            skills_error.value = `Debes elegir exactamente ${skill_limit.value} competencias nuevas`
            return false
        }

        saving_skills.value = true
        skills_error.value = ''
        try {
            const eligibleIds = new Set((class_detail.value?.skills ?? []).map(s => s.id))

            const nonClassSkills = (character.value?.skills ?? [])
                .filter(cs => !eligibleIds.has(cs.skill.id))
                .map(cs => ({ skillId: cs.skill.id, proficient: cs.proficient, expertise: cs.expertise }))

            const classSkills = (class_detail.value?.skills ?? []).map(s => ({
                skillId: s.id,
                proficient: isPregranted(s.id) || selected_skill_ids.value.includes(s.id),
                expertise: false
            }))

            const merged = [...nonClassSkills, ...classSkills]

            const res = await fetch(`${API_BASE}/characters/${props.characterId}/skills`, {
                method: 'PATCH',
                headers: {
                    Authorization: `Bearer ${props.token}`,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(merged)
            })
            if (!res.ok) {
                skills_error.value = 'Error al guardar las competencias'
                return false
            }
            await fetchCharacter()
            return true
        } catch (e) {
            console.error(e)
            skills_error.value = 'Error de conexión'
            return false
        } finally {
            saving_skills.value = false
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
            // 1) Guardamos las skills de clase elegidas
            const skillsOk = await saveClassSkills()
            if (!skillsOk) {
                error.value = skills_error.value || 'Error al guardar las competencias'
                return
            }
            // 2) Guardamos maxHp/currentHp calculados
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

            // 3) Finalizamos (el backend sube nivel 0->1 y marca status)
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

            // 4) Volvemos al listado de personajes
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

            <div v-if="class_detail" class="finalize_class_skills">
                <h2>Competencias de Clase ({{ selected_skill_ids.length }} / {{ skill_limit }})</h2>
                <span v-if="skills_error" class="finalize_skills_error">{{ skills_error }}</span>
                <div class="finalize_skill_list">
                    <label v-for="s in class_detail.skills" :key="s.id" class="finalize_skill_row">
                        <input type="checkbox" :checked="isSkillSelected(s.id)" :disabled="isPregranted(s.id) || (!isSkillSelected(s.id) && !canToggleOn())" @change="toggleSkill(s.id)"/>
                            {{ s.skill }} <em>({{ s.ability }})</em>
                            <span v-if="isPregranted(s.id)" class="finalize_skill_from_background">(ya adquirida por trasfondo)</span>
                        </label>
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

        <button class="character_finalize_save_btn" @click="finalizeCharacter" :disabled="saving || calculated_max_hp === null || character.level !== 0">
            {{ saving ? 'Finalizando...' : 'Finalizar y Guardar' }}
        </button>

        <button class="character_finalize_back" @click="goBackToCharacters">Volver a mis personajes</button>
    </div>
</template>

<style>

</style>