<script setup>
    //import '@/styles/appCharacterLevelUp.css'
    import { ref, computed, onMounted, watch } from 'vue'
    const props = defineProps({ token: String, characterId: { type: [Number, String], required: true } })
    const emit = defineEmits(['navigate'])
    const API_BASE = 'http://localhost:8080/api'

    const character = ref(null)
    const loading_character = ref(false)
    const all_classes = ref([])
    const selected_class_id = ref(null) 
    const hp_roll = ref(null)            


    const saving = ref(false)
    const error = ref('')

    async function fetchCharacter() {
        loading_character.value = true
        try {
            const res = await fetch(`${API_BASE}/characters/${props.characterId}`, {
                headers: { Authorization: `Bearer ${props.token}` }
            })
            character.value = await res.json()

            selected_class_id.value = character.value?.classes?.[0]?.classEntity?.id ?? null
        } catch (e) {
            console.error(e)
        } finally {
            loading_character.value = false
        }
    }

    async function fetchClasses() {
        try {
            const res = await fetch(`${API_BASE}/classes?size=50`, {
                headers: { Authorization: `Bearer ${props.token}` }
            })
            if (!res.ok) throw new Error(`GET /classes -> ${res.status}`)
            const data = await res.json()
            all_classes.value = Array.isArray(data) ? data : (data.content ?? [])
        } catch (e) {
            console.error(e)
            all_classes.value = []
        }
    }

    async function goToSubclassList(){
        emit('navigate', {page: 'characterSubclass', characterId: props.characterId, classId: selected_class_id.value})
    }

    onMounted(() => {
        fetchCharacter()
        fetchClasses()
    })

    //--- Clases: existentes vs. nuevas ---
    const current_classes = computed(() => character.value?.classes ?? [])

    const new_classes = computed(() =>
        all_classes.value.filter(c => !current_classes.value.some(cc => cc.classEntity.id === c.id))
    )

    // Entrada de character.classes si la clase ya la tengo; undefined si es nueva
    const selected_existing = computed(() =>
        current_classes.value.find(c => c.classEntity.id === selected_class_id.value)
    )

    const is_new_class = computed(() => selected_class_id.value !== null && !selected_existing.value)

    const selected_class_entity = computed(() =>
        selected_existing.value?.classEntity
        ?? all_classes.value.find(c => c.id === selected_class_id.value)
        ?? null
    )

    const new_class_level = computed(() => (selected_existing.value?.level ?? 0) + 1)

    // La subclase se elige antes de pasar a nivel 3 en esa clase
    const needs_subclass = computed(() =>
        !!selected_existing.value && selected_existing.value.level === 2 && !selected_existing.value.subclass
    )

    //--- Cálculo de HP máxima (regla 2024: valor del dado + mod CON) ---
    const hit_die_max = computed(() => {
        const die = selected_class_entity.value?.hitPointDie
        if (!die) return null
        const match = die.match(/d(\d+)/i)
        return match ? parseInt(match[1], 10) : null
    })

    const con_modifier = computed(() => modifierOf('CON'))

    // Tiramos el dado UNA vez cada vez que cambia la clase elegida
    watch(selected_class_id, () => {
       hp_roll.value = hit_die_max.value ? rollHitDie(hit_die_max.value) : null
    }, { immediate: true })

    const hp_gain = computed(() =>
        hp_roll.value === null ? null : Math.max(1, hp_roll.value + con_modifier.value)
    )

    function rollHitDie(sides) {
        return Math.floor(Math.random() * sides) + 1
    }

   const calculated_max_hp = computed(() =>
       hp_gain.value === null ? null : (character.value?.maxHp ?? 0) + hp_gain.value
   )
    
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
        if(needs_subclass.value){
            error.value = 'Subclass needed to proceed'
            return
        }
        if (character.value.level >= 20) {
            error.value = 'Nivel máximo alcanzado'
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

            // 2) Finalizamos (el backend sube nivel y marca status) /characters/{id}/classes/{classId}/level-up
            let levelRes
            if (is_new_class.value) {
                levelRes = await fetch(`${API_BASE}/characters/${props.characterId}/classes/${selected_class_id.value}`, {
                    method: 'POST',
                    headers: { Authorization: `Bearer ${props.token}`, 'Content-Type': 'application/json' },
                    body: JSON.stringify({ classId: selected_class_id.value })
                })
            } else {
                levelRes = await fetch(
                    `${API_BASE}/characters/${props.characterId}/classes/${selected_class_id.value}/level-up`,
                    { method: 'PATCH', headers:  {Authorization: `Bearer ${props.token}`} }
                )
            }
            if (!levelRes.ok) {
                error.value = 'Error al subir de nivel'
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
            <span class="level_up_level">Nivel total: {{ character.level }} -> {{ character.level + 1 }}</span>

            <div class="level_up_section">
                <span class="level_up_label">Especie:</span>
                <span>{{ character.species?.name }}</span>
            </div>

            <div class="level_up_section">
                <span class="level_up_label">Clases actuales:</span>
                <ul class="level_up_class_list">
                    <li v-for="c in character.classes" :key="c.classEntity.id">
                        {{ c.classEntity.name }} ({{ c.classEntity.hitPointDie }}) — Nivel {{ c.level }}
                        <span v-if="c.subclass"> / {{ c.subclass.name }}</span>
                    </li>
                </ul>
            </div>

            <!-- selector de clase -->
            <div class="level_up_section">
                <span class="level_up_label">Subir nivel en:</span>
                <select v-model="selected_class_id" class="level_up_class_select">
                    <optgroup label="Mis clases">
                        <option v-for="c in character.classes" :key="c.classEntity.id" :value="c.classEntity.id">
                            {{ c.classEntity.name }} (Nivel {{ c.level }} -> {{ c.level + 1 }})
                        </option>
                    </optgroup>
                    <optgroup label="Multiclase (nueva clase)">
                        <option v-for="c in new_classes" :key="c.id" :value="c.id">
                            {{ c.name }} (Nivel 1)
                        </option>
                    </optgroup>
                </select>
                <p v-if="selected_class_entity">
                    {{ selected_class_entity.name }}: nivel {{ new_class_level }}
                    <span v-if="is_new_class">(nueva clase)</span>
                </p>
            </div>

            <div class="level_up_section">
                <span class="level_up_label">Trasfondo:</span>
                <span>{{ character.background?.name }}</span>
            </div>

            <div class="level_up_hp_preview">
                <span>Hp:</span>
                <span>{{ character.maxHp }} -> {{ calculated_max_hp }}</span>
                <span v-if="hp_gain !== null">(+{{ hp_gain }})</span>
            </div>

            <p v-if="error" class="level_up_error">{{ error }}</p>
        </div>

        <button v-if="needs_subclass" class="character_subclass_btn" @click="goToSubclassList()">Subclass</button>
        <button class="character_level_up_save_btn" @click="levelUpCharacter"
                :disabled="saving || calculated_max_hp === null || character.level >= 20">
            {{ saving ? 'Finalizando...' : 'Level Up' }}
        </button>
        <button class="character_level_up_back" @click="goBackToCharacters">Volver a mis personajes</button>
    </div>
</template>

<style>

</style>