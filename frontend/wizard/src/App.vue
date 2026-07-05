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
  const backMap = {spells: 'main', items: 'main', classes: 'main', classExtended: 'classes', subclassExtended: 'classExtended'}
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

  function goBack() {
    currentPage.value = backMap[currentPage.value] ?? 'main'
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
    <div v-if="authToken" class="app_components">
      <header class="app_header">
        <button v-if="currentPage !== 'main'" class="header_back_btn" @click="goBack">Back</button>
        <span class="header_title">{{ currentPage }}</span>
        <button class="header_logout_btn" @click="logout">Log Out</button>
      </header>
      <div class="app_content">
        <AppMain v-if="currentPage === 'main'" @navigate="currentPage = $event" @logout="logout" :token="authToken"/>
        <AppSpells v-if="currentPage === 'spells'" :token="authToken"/>
        <AppItems v-if="currentPage === 'items'"  :token="authToken"/>
        <AppClasses v-if="currentPage === 'classes'"  @navigate="handleNavigate" :token="authToken"/>
        <AppClassExpansion v-if="currentPage === 'classExtended'" :classId="selectedClassId" @navigate="handleNavigate" :token="authToken"></AppClassExpansion>
        <AppSubclassExpansion v-if="currentPage === 'subclassExtended'" :subclassId="selectedSubclassId" :token="authToken"></AppSubclassExpansion>
      </div>
    </div>
  </transition>

</template>

<style>
  .app_header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 10px 20px;
    background: rgb(220, 220, 220);
    position: sticky;
    top: 0;
    z-index: 100;
  }
  .header_back_btn, .header_logout_btn {
    padding: 6px 12px;
    border: 1px solid #666;
    border-radius: 4px;
    background: white;
    cursor: pointer;
  }
  .header_title {
    font-weight: bold;
    text-transform: capitalize;
  }
</style>

