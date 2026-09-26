<script setup>
    import './styles/appMain.css'

    import { ref } from 'vue'
    defineEmits(['logout', 'navigate'])

</script>

<template>
    <div class="success_card">
        <div class="success_icon">🎲</div>
        <h1 class="title">¡Welcome!</h1>
        <button @click = "$emit('navigate', 'spells')">Spells</button>
        <button @click = "$emit('navigate', 'items')">Items</button>
        <button @click = "$emit('navigate', 'classes')">Classes</button>
        <button @click = "$emit('navigate', 'species')">Species</button>
        <button @click = "$emit('navigate', 'backgrounds')">Backgrounds</button>
        <button @click="$emit('navigate', 'characters')">Characters</button>
    </div>
</template>