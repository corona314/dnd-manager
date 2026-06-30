<script setup>
    import './styles/appClasses.css'
    import { ref} from 'vue'
    const props = defineProps({ token: String })
    const API_BASE = 'http://localhost:8080/api'
    defineEmits(['back'])
</script>

<template>
    <h1>Hello Hello</h1>
    <button @click="$emit('back')">Back</button>
</template>

<style>

</style>