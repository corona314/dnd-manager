<script setup>
import AppCharacterCreate from './AppCharacterCreate.vue';
import './styles/appCharacters.css'
import { ref, onMounted } from 'vue'
const props = defineProps({ token: String })
const emit = defineEmits(['navigate'])
const API_BASE = 'http://localhost:8080/api'

const characters = ref([])
const loading = ref(false)
const newCharacter = ref(false)

async function fetchCharacters() {
    loading.value = true
    try {
        const res = await fetch(`${API_BASE}/characters/me`, {
            headers: { Authorization: `Bearer ${props.token}` }
        })
        characters.value = await res.json()
    } catch (e) {
        console.error(e)
    } finally {
        loading.value = false
    }
}

async function deleteCharacter(id) {
    await fetch(`${API_BASE}/characters/${id}`, {
        method: 'DELETE',
        headers: { Authorization: `Bearer ${props.token}` }
    })
    fetchCharacters()
}

function createCharacter(){
    newCharacter.value = !newCharacter.value
}

function onCharacterCreated(newId) {
    newCharacter.value = false
    emit('navigate', { page: 'characterClass', characterId: newId })
}

function determineResumeStep(character) {
    if (!character.classEntity) return 'characterClass'
    if (!character.species) return 'characterSpecie'
    if (!character.background) return 'characterBackground'
    if (!character.abilities?.length) return 'characterAbilities'
    if (character.status !== 'FINAL') return 'characterFinalize'
    return 'characterClass'
}

async function goToEditCharacter(id) {  
    try {
        const res = await fetch(`${API_BASE}/characters/${id}`, {
            headers: { Authorization: `Bearer ${props.token}` }
        })
        const character = await res.json()
        const page = determineResumeStep(character)
        emit('navigate', { page, characterId: id })
    } catch (e) {
        console.error(e)
        emit('navigate', { page: 'characterClass', characterId: id })
    }
}

onMounted(() => fetchCharacters())
</script>

<template>
    <div class="characters_page">
        <h1>Mis Personajes</h1>
        <button @click="createCharacter">+ Nuevo personaje</button>
        <div v-if="loading">Cargando...</div>
        <div v-else class="characters_list">
            <div v-for="c in characters" :key="c.id" class="character_card">
                <span class="character_name">{{ c.name }}</span>
                <span class="character_status" :class="c.status === 'FINAL' ? 'status_completed' : 'status_draft'"> {{ c.status === 'FINAL' ? 'Completed' : 'Draft' }} </span>
                <span>{{ c.className === null ? 'no hay clase' : c.className}}</span>
                <span>{{ c.speciesName === null ? 'no hay especie' : c.speciesName}}</span>
                <button @click="deleteCharacter(c.id)">🗑️</button>
                <button v-if="c.status === 'DRAFT'" @click="goToEditCharacter(c.id)">Editar</button>
                <button v-if="c.status === 'FINAL'">Level Up</button>
                <button v-if="c.status === 'FINAL'">View Details</button>
            </div>
            <div v-if="!characters.length" class="characters_empty">
                No tienes personajes todavía.
            </div>
            <div v-if="newCharacter" class="character_name_creator" >
                <AppCharacterCreate :token="token" @created="onCharacterCreated"></AppCharacterCreate>
            </div>
        </div>
    </div>
</template>