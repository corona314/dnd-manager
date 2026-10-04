<script setup>
    import { ref } from 'vue'

    const API_BASE = 'http://localhost:8080/api'

    const emit = defineEmits(['login'])  

    const authMode = ref('login')
    const form = ref({ username: '', email: '', password: '' })
    const error = ref('')
    const loading = ref(false)

    async function submit() {
        error.value = ''
        loading.value = true
        try {
            const endpoint = authMode.value === 'login' ? '/auth/login' : '/auth/register'
            const res = await fetch(`${API_BASE}${endpoint}`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(form.value)
            })
            if (!res.ok) {
                const err = await res.json().catch(() => ({}))
                throw new Error(err.message || 'Error de autenticación')
            }
            const data = await res.json()
            emit('login', data.token, data.username || form.value.username)
        } catch (e) {
            error.value = e.message
        } finally {
            loading.value = false
        }
    }
</script>

<template>
    <div class="background">
        <div class="card">
            <div class="logo">🎲</div>
            <h1 class="name">WEB NAME</h1>
            <p class="subtitle">Very cute and good slogan</p>
            <div class="switch">
                <button class = "general_button" :class = "['tab_btn', { active: authMode === 'login' }]" @click="authMode = 'login'">Login</button>
                <button class = "general_button" :class = "['tab_btn', { active: authMode === 'register' }]" @click="authMode = 'register'">Register</button>
            </div>
            <div>
                <p>Username</p>
                <input v-model="form.username" type="text" placeholder="Adventurer Name" @keyup.enter="submit" >
            </div>
            
            <div v-if="authMode === 'register'">
                <p>Email</p>
                <input v-model="form.email" type="email" placeholder="Adventurer Email" @keyup.enter="submit">
            </div>

            <div>
                <p>Password</p>
                <input v-model="form.password" type="password" placeholder="••••••••" @keyup.enter="submit">
            </div>

            <div v-if="error" class="error">{{ authError }}</div>

            <button class="general_button" :disabled="authLoading" @click="submit">
                <span v-if="loading" class="spinner"></span>
                <span v-else>{{ authMode === 'login' ? 'Begin Adventure' : 'Join the Guild' }}</span>
            </button>
        </div>
    </div>
</template>

<style>
    
</style>