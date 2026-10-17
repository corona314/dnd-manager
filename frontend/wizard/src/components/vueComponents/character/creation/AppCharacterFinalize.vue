<script setup>
    import '@/styles/character/appCharacterFinalize.css'
    import CharacterStep from '@/vueComponents/shared/CharacterStep.vue'
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

            if (character.value?.classes?.[0]?.classEntity?.id) {
                await fetchClassDetail(character.value.classes[0].classEntity.id)
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
            skills_error.value = `You must choose exactly ${skill_limit.value} new skills`
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
                skills_error.value = 'Error saving class skills'
                return false
            }
            await fetchCharacter()
            return true
        } catch (e) {
            console.error(e)
            skills_error.value = 'Connection error'
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
        const die = character.value?.classes?.[0]?.classEntity?.hitPointDie // ej: "1d8"
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
            error.value = 'Health cannot be calculated because the character has no class assigned yet.'
            return
        }

        saving.value = true
        error.value = ''
        try {
            // 1) Guardamos las skills de clase elegidas
            const skillsOk = await saveClassSkills()
            if (!skillsOk) {
                error.value = skills_error.value || 'Error saving class skills'
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
                error.value = 'Error saving character health'
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
                const body = await finalizeRes.text()
                console.error('Finalize failed:', finalizeRes.status, body)
                error.value = body || 'Error finishing the character'
                return
            }

            // 4) Volvemos al listado de personajes
            emit('navigate', 'characters')
        } catch (e) {
            console.error(e)
            error.value = 'Connection error'
        } finally {
            saving.value = false
        }
    }
    function goBack() {
        const isMagical = class_detail.value?.resources?.some(
            resource => resource?.name === 'Cantrips' || resource?.name === 'Prepared Spells'
        ) ?? false

        emit('navigate', {
            page: isMagical ? 'characterSpells' : 'characterEquipment',
            characterId: props.characterId
        })
    }
    
</script>

<template>
    <CharacterStep
        :error="error"
        :continue-label="saving ? 'Finishing...' : 'Finish and Save'"
        :continue-disabled="saving || calculated_max_hp === null || character?.level !== 1"
        @back="goBack"
        @continue="finalizeCharacter"
    >
        <!--Cabecera con info del personaje-->
        <template #header>
            <div v-if="loading_character">Loading Character...</div>
            <div v-else-if="character" class="character_finalize_header">
                <h1>{{ character.name }}</h1>
                <span class="header_subtitle">Current Level: {{ character.level - 1 }} → 1</span>
            </div>
        </template>

        <template v-if="character">
            <!--Resumen: especie, clase y trasfondo-->
            <div class="finalize_card">
                <div class="finalize_section">
                    <span class="finalize_label">Species:</span>
                    <span>{{ character.species?.name ?? 'Not assigned' }}</span>
                </div>

                <div class="finalize_section">
                    <span class="finalize_label">Class:</span>
                    <span>{{ character.classes?.[0]?.classEntity?.name ?? 'Not assigned' }} ({{ character.classes?.[0]?.classEntity?.hitPointDie ?? '—' }})</span>
                </div>

                <div class="finalize_section">
                    <span class="finalize_label">Background:</span>
                    <span>{{ character.background?.name ?? 'Not assigned' }}</span>
                </div>
            </div>

            <!--Características-->
            <div class="finalize_card finalize_abilities">
                <h2>Abilities</h2>
                <div v-for="ab in character.abilities" :key="ab.ability" class="finalize_ability_row">
                    <span class="finalize_ability_code">{{ ab.ability }}</span>
                    <span class="finalize_ability_value">{{ ab.baseValue }}</span>
                    <span class="finalize_ability_mod">({{ formatModifier(modifierOf(ab.ability)) }})</span>
                </div>
            </div>

            <!--Habilidades de clase a elegir-->
            <div v-if="class_detail" class="finalize_card finalize_class_skills">
                <h2>Class Skills ({{ selected_skill_ids.length }} / {{ skill_limit }})</h2>
                <div class="finalize_skill_list">
                    <label v-for="s in class_detail.skills" :key="s.id" class="finalize_skill_row">
                        <input type="checkbox" :checked="isSkillSelected(s.id)" :disabled="isPregranted(s.id) || (!isSkillSelected(s.id) && !canToggleOn())" @change="toggleSkill(s.id)"/>
                        <span>{{ s.skill }} <em>({{ s.ability }})</em></span>
                        <span v-if="isPregranted(s.id)" class="finalize_skill_from_background">(acquired from background)</span>
                    </label>
                </div>
            </div>

            <!--Vida calculada-->
            <div class="finalize_card finalize_hp_preview">
                <h2>Health (calculated)</h2>
                <span v-if="calculated_max_hp !== null">
                    {{ hit_die_max }} (die) {{ formatModifier(con_modifier) }} (CON) = <strong>{{ calculated_max_hp }} HP</strong>
                </span>
                <span v-else class="finalize_hp_warning">
                    It cannot be calculated because the character has no class assigned yet.
                </span>
            </div>
        </template>
    </CharacterStep>
</template>

<style scoped>
    /* ---------- Cabecera ---------- */
    .character_finalize_header {
        display: flex;
        align-items: center;
        justify-content: center;
        flex-wrap: wrap;
        gap: 6px 16px;
    }

    .character_finalize_header h1 {
        margin: 0;
        font-size: 1.5rem;
    }

    /* ---------- Tarjetas ---------- */
    .finalize_card {
        display: flex;
        flex-direction: column;
        gap: 8px;
        width: 100%;
        box-sizing: border-box;
        padding: 16px;
        border: 1px solid var(--border);
        border-radius: 10px;
        background: var(--surface);
    }

    .finalize_card h2 {
        margin: 0 0 4px;
        font-size: 1.1rem;
    }

    /* Especie / clase / trasfondo */
    .finalize_section {
        display: flex;
        justify-content: space-between;
        gap: 12px;
        padding: 4px 0;
        border-bottom: 1px solid var(--separator-lines);
    }

    .finalize_section:last-child {
        border-bottom: none;
    }

    .finalize_label {
        font-weight: bold;
    }

    /* Características: tres por fila */
    .finalize_abilities {
        display: grid;
        grid-template-columns: repeat(3, minmax(0, 1fr));
        gap: 8px;
    }

    .finalize_abilities h2 {
        grid-column: 1 / -1;
    }

    .finalize_ability_row {
        display: flex;
        align-items: center;
        justify-content: space-between;
        gap: 8px;
        padding: 6px 10px;
        border: 1px solid var(--separator-lines);
        border-radius: 6px;
    }

    .finalize_ability_code {
        font-size: 0.8rem;
        font-weight: 700;
        letter-spacing: 0.08em;
        color: var(--text-secondary);
    }

    .finalize_ability_value {
        font-size: 1.1rem;
        font-weight: 700;
        margin-left: auto;
    }

    .finalize_ability_mod {
        min-width: 2.5em;
        text-align: right;
        font-size: 0.85rem;
    }

    /* Habilidades de clase */
    .finalize_skill_list {
        display: grid;
        grid-template-columns: repeat(auto-fill, minmax(230px, 1fr));
        gap: 6px;
    }

    .finalize_skill_row {
        display: flex;
        align-items: center;
        gap: 8px;
        padding: 4px 8px;
        border-radius: 6px;
        cursor: pointer;
    }

    .finalize_skill_row:hover {
        background: var(--surface-hover-strong);
    }

    .finalize_skill_row em {
        color: var(--text-secondary);
        font-size: 0.85em;
    }

    .finalize_skill_from_background {
        margin-left: auto;
        font-size: 0.75rem;
        color: var(--text-secondary);
    }

    /* Vida */
    .finalize_hp_preview strong {
        color: var(--accent);
    }

    .finalize_hp_warning {
        color: var(--danger);
    }

    @media (max-width: 600px) {
        .finalize_abilities {
            grid-template-columns: repeat(2, minmax(0, 1fr));
        }
    }
</style>