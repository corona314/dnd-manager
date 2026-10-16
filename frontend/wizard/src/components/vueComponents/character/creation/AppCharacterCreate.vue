<script setup>
import { ref } from 'vue'
const props = defineProps({ token: String })
const emit = defineEmits(['created'])
const API_BASE = 'http://localhost:8080/api'

const name = ref('')
const error = ref('')
const saving = ref(false)

async function createCharacter() {
    if (!name.value.trim()) {
        error.value = 'Name cannot be empty'
        return
    }
    saving.value = true
    try {
        const res = await fetch(`${API_BASE}/characters`, {
            method: 'POST',
            headers: {
                Authorization: `Bearer ${props.token}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ name: name.value.trim() })
        })
        if (!res.ok) {
            error.value = 'Error creating character'
        }else{
            const created = await res.json()
            emit('created', created.id)
        }
    } catch (e) {
        console.error(e)
        error.value = 'Connection error'
    } finally {
        saving.value = false
    }
}
</script>

<template>
    <div class="character_create_page">
        <h1>New Character</h1>
        <div class="character_create_form">
            <input type="text" v-model="name" placeholder="Character name" @keyup.enter="createCharacter"/>
            <span v-if="error" class="character_create_error">{{ error }}</span>
            <button class="general_button" @click="createCharacter" :disabled="saving">
                {{ saving ? 'Saving...' : 'Create' }}
            </button>
        </div>
    </div>
</template>