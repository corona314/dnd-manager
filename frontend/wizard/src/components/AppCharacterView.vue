<script setup>
    //import './styles/appCharacterView.css'
    import { ref, computed, onMounted } from 'vue'
    const props = defineProps({ token: String, characterId: { type: [Number, String], required: true } })
    const emit = defineEmits(['navigate'])
    const API_BASE = 'http://localhost:8080/api'

    const character = ref(null)
    const loading_character = ref(false)

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

    function goBackToCharacters() {
        emit('navigate', 'characters')
    }
</script>

<template>
    <div class="character_finalize_page">
        <div v-if="loading_character">Cargando personaje...</div>

        <div v-else-if="character" class="character_finalize_summary">
            <h1>{{ character.name }}</h1>
            <span class="finalize_level">Nivel actual: {{ character.level }}</span>

            <div class="finalize_section">
                <span class="finalize_label">Especie:</span>
                <span>{{ character.species?.name}}</span>
            </div>

            <div class="finalize_section">
                <span class="finalize_label">Clase:</span>
                <span>{{ character.classEntity?.name }} ({{ character.classEntity?.hitPointDie}})</span>
            </div>

            <div class="finalize_section">
                <span class="finalize_label">Trasfondo:</span>
                <span>{{ character.background?.name }}</span>
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
                <span>Hp:</span>
                <span>{{ character.maxHp }}/{{ character.currentHp }}</span>
            </div>
        </div>
        <button class="character_finalize_back" @click="goBackToCharacters">Volver a mis personajes</button>
    </div>
</template>

<style>

</style>