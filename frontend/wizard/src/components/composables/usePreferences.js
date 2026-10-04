import { ref, watch } from 'vue'

const KEY = 'dnd_use_icons'

export const usingIcons = ref(localStorage.getItem(KEY) !== 'off')

watch(usingIcons, (v) => localStorage.setItem(KEY, v ? 'on' : 'off'))