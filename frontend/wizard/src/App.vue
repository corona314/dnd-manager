<script setup>
  //Componentes
  import AppItems from './components/AppItems.vue';
  import AppLogin from './components/AppLogin.vue';
  import AppMain from './components/AppMain.vue';
  import AppSpells from './components/AppSpells.vue';

  import { ref, onMounted } from 'vue'

  const authToken = ref(localStorage.getItem('dnd_token') || '')
  const currentPage = ref('main')  // 'main' | 'spells' | 'items'

  function handleLogin(token) {
    authToken.value = token
    localStorage.setItem('dnd_token', token) 
      
  }

  function logout() {
    authToken.value = ''
    localStorage.removeItem('dnd_token')
    characters.value = []
    selectedId.value = null
    selectedChar.value = null
    showCreate.value = false
    showEdit.value = false
    currentPage.value = 'main'
  }

</script>

<template>
  <transition name="fade">
    <AppLogin v-if="!authToken" @login="handleLogin" />
  </transition>

  <transition name="fade">
    <div v-if="authToken" class="success-screen">
      <AppMain v-if="currentPage === 'main'" @navigate="currentPage = $event" @logout="logout"/>
      <AppSpells v-if="currentPage === 'spells'" @back="currentPage = 'main'"/>
      <AppItems v-if="currentPage === 'items'" @back="currentPage = 'main'"/>
    </div>
  </transition>

</template>

<style>

</style>

