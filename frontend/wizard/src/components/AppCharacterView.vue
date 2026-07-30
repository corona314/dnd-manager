<script setup>
    //import './styles/appCharacterView.css'
    import { ref, computed, onMounted } from 'vue'
    const props = defineProps({ token: String, characterId: { type: [Number, String], required: true } })
    const emit = defineEmits(['navigate'])
    const API_BASE = 'http://localhost:8080/api'

    const character = ref(null)
    const loading_character = ref(false)

    const PROFICIENCY_BONUS = {1:2, 2:2, 3:2, 4:2, 5:3, 6:3, 7:3, 8:3, 9:4, 10:4, 11:4, 12:4, 13:5, 14:5, 15:5, 16:5, 17:6, 18:6, 19:6, 20:6}

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

    function formatModifier(mod) {
        return mod >= 0 ? `+${mod}` : `${mod}`
    }

    //--- Cálculo de modificador de Habilidades ---
    function proficiencyValue(){
        return PROFICIENCY_BONUS[character.value?.level]
    }

    function skillModifier(mod, proficient, expert){
        if(proficient) mod += proficiencyValue()
        else if(expert) mod += proficiencyValue() * 2
        return mod >= 0 ? `+${mod}` : `${mod}`
    }

    function goBackToCharacters() {
        emit('navigate', 'characters')
    }
</script>

<template>
    <div class="character_view_page">
        <div v-if="loading_character">Cargando personaje...</div>

        <div v-else-if="character" class="character_view_summary">
            <h1>{{ character.name }}</h1>
            <span class="view_level">Current Level: {{ character.level }}</span>

            <div class="view_section">
                <span class="view_label">Specie:</span>
                <span>{{ character.species?.name}}</span>
            </div>

            <div class="view_section">
                <span class="view_label">Class:</span>
                <span>{{ character.classEntity?.name }} ({{ character.classEntity?.hitPointDie}})</span>
            </div>

            <div class="view_section">
                <span class="view_label">Background:</span>
                <span>{{ character.background?.name }}</span>
            </div>

            <div v-if="character?.subclass !== null" class="view_section">
                <span class="view_label">Subclass:</span>
                <span>{{ character.subclass?.name }}</span>
            </div>

            <div class="view_abilities">
                <h2>Abilities</h2>
                <div v-for="ab in character.abilities" :key="ab.ability" class="view_ability_row">
                    <span>{{ ab.ability }}</span>
                    <span>{{ ab.baseValue }}</span>
                    <span>({{ formatModifier(modifierOf(ab.ability)) }})</span>
                </div>
            </div>

            <div class="view_skills">
                <h2>Skills</h2>
                <div v-for="s in character.skills" :key="s.skill.id" class="view_skill_row">
                    <span>{{ s.skill.skill }}</span>
                    <span>{{ s.skill.ability }}</span>
                    <span v-if="s.proficient===true">*</span>
                    <span>({{ skillModifier(modifierOf(s.skill.ability), s.proficient, s.expertise) }})</span>
                </div>
            </div>

            <div class="view_hp_preview">
                <span>Hp:</span>
                <span>{{ character.maxHp }}/{{ character.currentHp }}</span>
            </div>
        </div>
        <button class="character_view_back" @click="goBackToCharacters">Volver a mis personajes</button>
    </div>
</template>

<style>

</style>