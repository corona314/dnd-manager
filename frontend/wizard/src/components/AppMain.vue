<script setup>
    import './styles/appMain.css'

    import { ref } from 'vue'
    defineEmits(['logout', 'navigate'])

</script>

<template>
    <div class="success_card">
        <div class="success_icon">🎲</div>
        <h1 class="title">¡Welcome!</h1>
        <button @click = "$emit('navigate', 'spells')">Conjuros</button>
        <button @click = "$emit('navigate', 'items')">Objetos</button>
        <button @click = "$emit('navigate', 'classes')">Clases</button>
        <button @click = "$emit('navigate', 'species')">Razas</button>
        <button @click = "$emit('navigate', 'backgrounds')">Trasfondos</button>
        <button @click="$emit('logout')">Cerrar sesión</button>
    </div>
</template>