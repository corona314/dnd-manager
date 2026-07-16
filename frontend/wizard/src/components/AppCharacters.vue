<script setup>
import { ref, onMounted } from 'vue'
const props = defineProps({ token: String })
defineEmits(['navigate'])
const API_BASE = 'http://localhost:8080/api'

const characters = ref([])
const loading = ref(false)

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

onMounted(() => fetchCharacters())
</script>

<template>
    <div class="characters_page">
        <h1>Mis Personajes</h1>
        <button @click="$emit('navigate', 'characterCreate')">+ Nuevo personaje</button>
        <div v-if="loading">Cargando...</div>
        <div v-else class="characters_list">
            <div v-for="c in characters" :key="c.id" class="character_card">
                <span class="character_name">{{ c.name }}</span>
                <button @click="deleteCharacter(c.id)">🗑️</button>
            </div>
            <div v-if="!characters.length" class="characters_empty">
                No tienes personajes todavía.
            </div>
        </div>
    </div>
</template>