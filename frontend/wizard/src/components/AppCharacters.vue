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

function onCharacterCreated() {
    newCharacter.value = false
    fetchCharacters()
}

function goToEditCharacter(id) {  
    emit('navigate', { page: 'characterClass', characterId: id })
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
                <span class="character_status" :class="c.status === 'COMPLETED' ? 'status_completed' : 'status_draft'"> {{ c.status === 'COMPLETED' ? 'Completated' : 'Draft' }} </span>
                <span>{{ c.className === null ? 'no hay clase' : c.className}}</span>
                <button @click="deleteCharacter(c.id)">🗑️</button>
                <button @click="goToEditCharacter(c.id)">Editar</button>
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