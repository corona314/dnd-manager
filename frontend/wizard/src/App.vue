<script setup>
  //Componentes
  import AppClasses from './components/AppClasses.vue';
  import AppClassExpansion from './components/AppClassExpansion.vue';
  import AppItems from './components/AppItems.vue';
  import AppLogin from './components/AppLogin.vue';
  import AppMain from './components/AppMain.vue';
  import AppSpells from './components/AppSpells.vue';

  import { ref, onMounted } from 'vue'
  import AppSubclassExpansion from './components/AppSubclassExpansion.vue';

  const authToken = ref(localStorage.getItem('dnd_token') || '')
  const currentPage = ref('main')  // 'main' | 'spells' | 'items' | 'classes' | 'classExtended' | 'subclassExtended'
  const selectedClassId = ref(null)
  const selectedSubclassId = ref(null)

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
    selectedClassId.value = null
    selectedSubclassId.value = null
  }

  function handleNavigate(event) {
    if (typeof event === 'string') {
      currentPage.value = event
      return
    } 
    currentPage.value = event.page
    if ('classId' in event) selectedClassId.value = event.classId
    if ('subclassId' in event) selectedSubclassId.value = event.subclassId
  }
</script>

<template>
  <transition name="fade">
    <AppLogin v-if="!authToken" @login="handleLogin" />
  </transition>

  <transition name="fade">
    <div v-if="authToken" class="success-screen">
      <AppMain v-if="currentPage === 'main'" @navigate="currentPage = $event" @logout="logout" :token="authToken"/>
      <AppSpells v-if="currentPage === 'spells'" @back="currentPage = 'main'" :token="authToken"/>
      <AppItems v-if="currentPage === 'items'" @back="currentPage = 'main'" :token="authToken"/>
      <AppClasses v-if="currentPage === 'classes'" @back="currentPage = 'main'" @navigate="handleNavigate" :token="authToken"/>
      <AppClassExpansion v-if="currentPage === 'classExtended'" @back="currentPage = 'classes'" :classId="selectedClassId" @navigate="handleNavigate" :token="authToken"></AppClassExpansion>
      <AppSubclassExpansion v-if="currentPage === 'subclassExtended'" @back="currentPage = 'classExtended'" :subclassId="selectedSubclassId" :token="authToken"></AppSubclassExpansion>
    </div>
  </transition>

</template>

<style>

</style>

