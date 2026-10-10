import { watch, onMounted, onBeforeUnmount } from 'vue'
import { blob, say, sayOnce, hush } from './useBlobSay.js'
import { blobTalks } from './usePreferences.js'

// Solo hablan las pantallas que aparecen aquí (de momento, los compendios).
// Las claves son los valores de currentPage en App.vue.

// Consejo de primera visita: sale una sola vez por pantalla
const TIPS = {
    spells: 'Tip: TIP SUPER IMPORTANTE DE SPELLS.',
    items: 'Tip: TIP SUPER IMPORTANTE DE ITEMS.',
    classes: 'Tip: TIP SUPER IMPORTANTE DE CLASSES.',
    species: 'Tip: TIP SUPER IMPORTANTE DE SPECIES.',
    backgrounds: 'Tip: TIP SUPER IMPORTANTE DE BACKGROUNDS.'
}

// Frases sueltas: salen de vez en cuando, nunca la misma dos veces seguidas
const CHATTER = {
    spells: [
        'You can open several spell cards at once to compare them.',
        'Right-click a filter to cycle it backwards.'
    ],
    items: [
        'You can open several item cards at once to compare them.',
        'Weapon properties have a tooltip: hover over them.',
    ],
    classes: [
        'Open a few classes to compare their saving throws.',
        
    ],
    species: [
        'Open a couple of species to compare their speeds.'
    ],
    backgrounds: [
        'Each background lists the abilities it relates to.'
    ]
}

const MIN_DELAY = 2 * 60 * 1000   // 2 min
const MAX_DELAY = 5 * 60 * 1000   // 5 min
const TIP_DELAY = 800             // pequeña pausa tras cambiar de pantalla

const rand = (min, max) => min + Math.random() * (max - min)

// page: ref con la pantalla actual
// isBusy: () => true cuando NO conviene hablar (sin sesión, ajustes abiertos...)
export function useBlobChatter(page, isBusy = () => false) {
    let chatTimer
    let tipTimer
    let last = ''

    function canTalk() {
        return blobTalks.value && !document.hidden && !blob.message && !isBusy()
    }

    function chat() {
        const pool = CHATTER[page.value]
        if (pool?.length && canTalk()) {
            const options = pool.length > 1 ? pool.filter(m => m !== last) : pool
            last = options[Math.floor(Math.random() * options.length)]
            say(last)
        }
        schedule()
    }

    function schedule() {
        chatTimer = setTimeout(chat, rand(MIN_DELAY, MAX_DELAY))
    }

    // Consejo de primera visita
    watch(page, (p) => {
        clearTimeout(tipTimer)
        if (!TIPS[p]) return
        tipTimer = setTimeout(() => {
            if (canTalk()) sayOnce(`tip-${p}`, TIPS[p], { mood: ['mad', 'sad', 'thinking'][Math.floor(Math.random() * 3)]})
        }, TIP_DELAY)
    }, { immediate: true })

    // Al apagar la opción se calla
    watch(blobTalks, (on) => { if (!on) hush() })

    onMounted(schedule)
    onBeforeUnmount(() => {
        clearTimeout(chatTimer)
        clearTimeout(tipTimer)
    })
}