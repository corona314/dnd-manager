<script setup>

import ToggleSwitch from 'primevue/toggleswitch'
import AppAvatar from './AppAvatar.vue'
import { usingIcons } from '@/composables/usePreferences.js'

const isDark = defineModel('dark')
const emit = defineEmits(['logout', 'toggle-theme'])
defineProps({ username: String })

</script>

<template>
  <section class="settings" v-no-double-select>

    <div class="settings_row">
      <span>Username</span>
      <strong>{{ username }}</strong>
    </div>

    <div class="settings_avatar">
      <AppAvatar :name="username" />
    </div>

    <div class="settings_row">
      <span>Use Icons</span>
      <ToggleSwitch v-model="usingIcons" />
    </div>

    <div class="settings_row">
      <span>Dark Mode</span>
      <ToggleSwitch v-model="isDark" @change="emit('toggle-theme')" />
    </div>

    <!-- aquí luego: cambiar nombre, contraseña... esas cosas de configuración -->

    <button class="settings_logout_btn" @click="emit('logout')">Logout</button>
  </section>
</template>

<style scoped>

  .settings {

    display: flex;
    flex-direction: column;
    gap: 1rem;
  }

  .settings_row {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .settings_logout_btn {
    background: #da767a;
    color: white;
    border: none;
    padding: 8px 16px;
    border-radius: 6px;
    cursor: pointer;
  }
</style>