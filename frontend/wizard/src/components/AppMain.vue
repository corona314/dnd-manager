<script setup>
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
        <button @click="$emit('logout')">Cerrar sesión</button>
    </div>
</template>

<style>
    @media (prefers-color-scheme: dark) {
        .success_card button:hover {
            filter: brightness(1.15);
        }
    }
    .success_card {
        display: flex;
        align-items: center;
        gap: 16px;
        padding: 32px;
        border-radius: 10px;
        background: var(--card-background);
        color: var(--text);
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
    }

    .success_card button {
        width: 220px;
        padding: 10px 16px;
        border: 1px solid var(--border);
        border-radius: 6px;
        background: var(--buttons);
        color: var(--text);
        cursor: pointer;
        transition: background-color 0.2s, transform 0.1s;
    }

    .success_card button:hover {
        filter: brightness(0.95);
    }

    .success_card button:active {
        transform: scale(0.98);
    }
</style>