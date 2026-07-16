<script setup>
  import './components/styles/app.css'
  //Componentes
  import AppClasses from './components/AppClasses.vue';
  import AppClassExpansion from './components/AppClassExpansion.vue';
  import AppItems from './components/AppItems.vue';
  import AppLogin from './components/AppLogin.vue';
  import AppMain from './components/AppMain.vue';
  import AppSpells from './components/AppSpells.vue';
  import AppSubclassExpansion from './components/AppSubclassExpansion.vue';
  import AppSpecies from './components/AppSpecies.vue';
  import AppBackgrounds from './components/AppBackgrounds.vue';
  import AppSpecieExpansion from './components/AppSpecieExpansion.vue';
  import AppBackgroundExpansion from './components/AppBackgroundExpansion.vue';
  import AppCharacters from './components/AppCharacters.vue';
  import AppCharacterCreate from './components/AppCharacterCreate.vue';

  import { ref, onMounted } from 'vue'


  
  const authToken = ref(localStorage.getItem('dnd_token') || '')
  const currentPage = ref('main')  // 'main' | 'spells' | 'items' | 'classes' | 'classExtended' | 'subclassExtended' | 'species' | 'specieExtended' | 'backgrounds' | 'backgroundExtended' | 'characters' | 'characterCreate'
  const backMap = {spells: 'main', items: 'main', classes: 'main', classExtended: 'classes', subclassExtended: 'classExtended', species: 'main', backgrounds: 'main', specieExtended: 'species', backgroundExtended: 'backgrounds', characters: 'main', characterCreate: 'characters'}
  const selectedClassId = ref(null)
  const selectedSubclassId = ref(null)
  const selectedSpecieId = ref(null)
  const selectedBackgroundId = ref(null)

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
    selectedSpecieId.value = null
    selectedBackgroundId.value = null
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
    if ('specieId' in event) selectedSpecieId.value = event.specieId
    if ('backgroundId' in event) selectedBackgroundId.value = event.backgroundId
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
        <AppMain v-if="currentPage === 'main'" @navigate="handleNavigate" @logout="logout" :token="authToken"/>
        <AppSpells v-if="currentPage === 'spells'" :token="authToken"/>
        <AppItems v-if="currentPage === 'items'"  :token="authToken"/>
        <AppClasses v-if="currentPage === 'classes'"  @navigate="handleNavigate" :token="authToken"/>
        <AppClassExpansion v-if="currentPage === 'classExtended'" :classId="selectedClassId" @navigate="handleNavigate" :token="authToken"></AppClassExpansion>
        <AppSubclassExpansion v-if="currentPage === 'subclassExtended'" :subclassId="selectedSubclassId" :token="authToken"></AppSubclassExpansion>
        <AppSpecies v-if="currentPage === 'species'" @navigate="handleNavigate" :token="authToken"></AppSpecies>
        <AppSpecieExpansion v-if="currentPage === 'specieExtended'" :specieId="selectedSpecieId" @navigate="handleNavigate" :token="authToken"></AppSpecieExpansion>
        <AppBackgrounds v-if="currentPage === 'backgrounds'" @navigate="handleNavigate" :token="authToken"></AppBackgrounds>
        <AppBackgroundExpansion v-if="currentPage === 'backgroundExtended'" :backgroundId="selectedBackgroundId" @navigate="handleNavigate" :token="authToken"></AppBackgroundExpansion>
        <AppCharacters v-if="currentPage === 'characters'" @navigate="handleNavigate" :token="authToken"/>
        <AppCharacterCreate v-if="currentPage === 'characterCreate'" @navigate="handleNavigate" :token="authToken"/>
      </div>
    </div>
  </transition>

</template>
