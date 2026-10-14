<script setup>
    const props = defineProps({currentClass: [String], characterId: [Number, String]})
    const emit = defineEmits(['navigate'])

    const backMap = {characterClass: 'characters', characterSpecie: 'characterClass', characterBackground: 'characterSpecie', characterAbilities: 'characterBackground', characterFinalize: 'characterSpells', characterView: 'characters', characterLevelUp: 'characters', characterSubclass: 'characterLevelUp', characterEquipment: 'characterAbilities', characterSpells: 'characterEquipment'}

    function goBack(){
        const backPage = backMap[props.currentClass]
        if (!backPage) return
        emit('navigate', { page: backPage, characterId: props.characterId })
    }
</script>

<template>
    <button @click="goBack()" class="general_button">
        Back
    </button>
</template>