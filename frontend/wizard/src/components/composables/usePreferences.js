import { ref, watch } from 'vue'



const KEY = 'dnd_use_icons'

export const usingIcons = ref(localStorage.getItem(KEY) !== 'off')

watch(usingIcons, (v) => localStorage.setItem(KEY, v ? 'on' : 'off'))



const TALK_KEY = 'dnd_blob_talks'

export const blobTalks = ref(localStorage.getItem(TALK_KEY) !== 'off')

watch(blobTalks, (v) => localStorage.setItem(TALK_KEY, v ? 'on' : 'off'))