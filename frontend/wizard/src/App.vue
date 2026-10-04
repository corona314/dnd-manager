<script setup>
  import './components/styles/app.css'
  import ToggleSwitch from 'primevue/toggleswitch'
  //Componentes
  import AppClasses from './components/vueComponents/compendiums/AppClasses.vue';
  import AppClassExpansion from './components/vueComponents/compendiums/AppClassExpansion.vue';
  import AppItems from './components/vueComponents/compendiums/AppItems.vue';
  import AppLogin from './components/vueComponents/main/AppLogin.vue';
  import AppMain from './components/vueComponents/main/AppMain.vue';
  import AppSpells from './components/vueComponents/compendiums/AppSpells.vue';
  import AppSubclassExpansion from './components/vueComponents/compendiums/AppSubclassExpansion.vue';
  import AppSpecies from './components/vueComponents/compendiums/AppSpecies.vue';
  import AppBackgrounds from './components/vueComponents/compendiums/AppBackgrounds.vue';
  import AppSpecieExpansion from './components/vueComponents/compendiums/AppSpecieExpansion.vue';
  import AppBackgroundExpansion from './components/vueComponents/compendiums/AppBackgroundExpansion.vue';
  import AppCharacters from './components/vueComponents/compendiums/AppCharacters.vue';
  import AppCharacterClass from './components/vueComponents/character/creation/AppCharacterClass.vue';
  import AppCharacterSpecie from './components/vueComponents/character/creation/AppCharacterSpecie.vue';
  import AppCharacterBackground from './components/vueComponents/character/creation/AppCharacterBackground.vue';
  import AppCharacterAbilities from './components/vueComponents/character/creation/AppCharacterAbilities.vue';
  import AppCharacterFinalize from './components/vueComponents/character/creation/AppCharacterFinalize.vue';
  import AppCharacterView from './components/vueComponents/character/management/AppCharacterView.vue';
  import AppCharacterLevelUp from './components/vueComponents/character/management/AppCharacterLevelUp.vue';
  import AppCharacterSubclass from './components/vueComponents/character/creation/AppCharacterSubclass.vue';
  import AppCharacterEquipment from './components/vueComponents/character/creation/AppCharacterEquipment.vue';
  import AppCharacterSpells1 from './components/vueComponents/character/creation/AppCharacterSpells1.vue';
  import AppAvatar from './components/vueComponents/main/AppAvatar.vue'
  import AppSettings from './components/vueComponents/main/AppSettings.vue'
  import Dialog from 'primevue/dialog'


  import { ref, onMounted } from 'vue'


  const username = ref(localStorage.getItem('dnd_username') || 'user')
  const authToken = ref(localStorage.getItem('dnd_token') || '')
  const currentPage = ref('main')  // 'main' | 'spells' | 'items' | 'classes' | 'classExtended' | 'subclassExtended' | 'species' | 'specieExtended' | 'backgrounds' | 'backgroundExtended' | 'characters' | 'characterClass' | 'characterSpecie' | 'characterBackground' | 'characterAbility' | 'characterFinalize' | 'characterView' | 'characterLevelUp' | 'characterSubclass' | 'characterEquipment' | 'characterSpells'
  const backMap = {spells: 'main', items: 'main', classes: 'main', classExtended: 'classes', subclassExtended: 'classExtended', species: 'main', backgrounds: 'main', specieExtended: 'species', backgroundExtended: 'backgrounds', characters: 'main', characterClass: 'characters', characterSpecie: 'characterClass', characterBackground: 'characterSpecie', characterAbilities: 'characterBackground', characterFinalize: 'characterSpells', characterView: 'characters', characterLevelUp: 'characters', characterSubclass: 'characterLevelUp', characterEquipment: 'characterAbilities', characterSpells: 'characterEquipment'}
  const selectedClassId = ref(null)
  const selectedSubclassId = ref(null)
  const selectedSpecieId = ref(null)
  const selectedBackgroundId = ref(null)
  const selectedCharacterId = ref(null)
  const showSettings = ref(false)
 
  function handleLogin(token, name) {
    username.value = name || 'user'
    authToken.value = token
    localStorage.setItem('dnd_token', token)
    localStorage.setItem('dnd_username', username.value)
  }

  function logout() {
    showSettings.value = false
    authToken.value = ''
    username.value = 'user'
    localStorage.removeItem('dnd_token')
    localStorage.removeItem('dnd_username')
    currentPage.value = 'main'
    selectedClassId.value = null
    selectedSubclassId.value = null
    selectedSpecieId.value = null
    selectedBackgroundId.value = null
    selectedCharacterId.value = null
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
    if ('characterId' in event) selectedCharacterId.value = event.characterId
  }

  //Metodos Dark-Mode

  const isDark = ref(
    localStorage.getItem('dnd_theme')
      ? localStorage.getItem('dnd_theme') === 'dark'
      : window.matchMedia('(prefers-color-scheme: dark)').matches
  )

  function applyTheme() {
    if (isDark.value) {
      document.documentElement.setAttribute('data-theme', 'dark')
    } else {
      document.documentElement.removeAttribute('data-theme')
    }
  }

  function toggleTheme() {
    localStorage.setItem('dnd_theme', isDark.value ? 'dark' : 'light')
    applyTheme()
  }

  onMounted(() => {
    applyTheme()
  })
</script>

<template>
  <transition name="fade">
    <AppLogin v-if="!authToken" @login="handleLogin" />
  </transition>

  <transition name="fade">
    <div v-if="authToken" class="app_components">

    <header class="app_header" v-no-double-select>
      <button v-if="currentPage !== 'main'" class="general_button" @click="goBack">Back</button>
      <span class="header_title">{{ currentPage }}</span>

      <button class="header_avatar_btn" @click="showSettings = true" title="Settings" v-no-double-select>
        <AppAvatar :name="username"/>
      </button>
    </header>

      <div class="app_content">
        <AppMain v-if="currentPage === 'main'" @navigate="handleNavigate" @logout="logout" :token="authToken" />
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
        <AppCharacterClass v-if="currentPage === 'characterClass'" :characterId="selectedCharacterId" @navigate="handleNavigate" :token="authToken"></AppCharacterClass>
        <AppCharacterSpecie v-if="currentPage === 'characterSpecie'" :characterId="selectedCharacterId" @navigate="handleNavigate" :token="authToken"></AppCharacterSpecie>
        <AppCharacterBackground v-if="currentPage === 'characterBackground'" :characterId="selectedCharacterId" @navigate="handleNavigate" :token="authToken"></AppCharacterBackground>
        <AppCharacterAbilities v-if="currentPage === 'characterAbilities'" :characterId="selectedCharacterId" @navigate="handleNavigate" :token="authToken"></AppCharacterAbilities>
        <AppCharacterFinalize v-if="currentPage === 'characterFinalize'" :characterId="selectedCharacterId" @navigate="handleNavigate" :token="authToken"></AppCharacterFinalize>
        <AppCharacterView v-if="currentPage === 'characterView'" :characterId="selectedCharacterId" @navigate="handleNavigate" :token="authToken"></AppCharacterView>
        <AppCharacterLevelUp v-if="currentPage === 'characterLevelUp'" :characterId="selectedCharacterId" @navigate="handleNavigate" :token="authToken"></AppCharacterLevelUp>
        <AppCharacterSubclass v-if="currentPage === 'characterSubclass'" :characterId="selectedCharacterId" :classId="selectedClassId" @navigate="handleNavigate" :token="authToken"></AppCharacterSubclass>
        <AppCharacterEquipment v-if="currentPage === 'characterEquipment'" :characterId="selectedCharacterId" @navigate="handleNavigate" :token="authToken"></AppCharacterEquipment>
        <AppCharacterSpells1 v-if="currentPage === 'characterSpells'" :characterId="selectedCharacterId" @navigate="handleNavigate" :token="authToken"></AppCharacterSpells1>
      </div>
      <Dialog v-model:visible="showSettings" modal dismissableMask header="Settings" :style="{ width: '25rem' }">
        <AppSettings :username="username" v-model:dark="isDark" @toggle-theme="toggleTheme" @logout="logout" />      
      </Dialog>
    </div>
  </transition>

</template>
