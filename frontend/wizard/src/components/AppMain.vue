<script setup>
    import { ref } from 'vue'

    defineEmits(['logout', 'navigate'])

</script>

<template>
    <div class="success_card">
        <div class="success_icon">🎲</div>
        <h1 class="title">¡Welcome!</h1>
        <button @click = "$emit('navigate', 'spells')">Conjuros</button>
        <button @click="$emit('logout')">Cerrar sesión</button>
    </div>
</template>

<style>
    
</style>