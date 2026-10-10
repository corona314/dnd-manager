import { reactive } from 'vue'

// Estado compartido: lo leen BlobBubble y el avatar.
//   id: cambia con cada mensaje (reinicia la barra de progreso)
//   duration: ms que durará el mensaje (0 = espera a que el usuario lo cierre)
export const blob = reactive({ message: '', mood: null, id: 0, duration: 0 })

const SEEN_KEY = 'dnd_blob_seen'
let timer
let moodTimer

function readSeen() {
    try {
        return JSON.parse(localStorage.getItem(SEEN_KEY) || '[]')
    } catch {
        return []
    }
}

// Hace que el blob diga algo
//   say('Hi')                                                            - duración según la longitud del texto
//   say('Hi', { mood: 'wink', duration: 1000 })                          - mensaje y mood duran 1 s
//   say('Hi', { mood: null })                                            - sin mood
//   say('Hi', { duration: 0 })                                           - se queda hasta que se pulse "Got it"
//   say('Hi', { mood: 'thinking', duration: 12000, moodDuration: 2000 }) - la cara dura menos que el mensaje
export function say(message, { mood = 'happy', duration } = {}) {
    clearTimeout(timer)

    const ms = duration ?? Math.max(4000, message.length * 100) // 180 palabras/minuto

    blob.id++
    blob.message = message
    blob.mood = mood
    blob.duration = ms > 0 ? ms : 0

    if (ms > 0) timer = setTimeout(hush, ms)
}


// Callarlo
export function hush() {
    clearTimeout(timer)
    blob.message = ''
    blob.mood = null
    blob.duration = 0
}

// Como say(), pero solo la primera vez para esa clave (se recuerda en localStorage)
// Devuelve true si lo dijo y false si ya lo había dicho antes
export function sayOnce(key, message, options) {
    const seen = readSeen()
    if (seen.includes(key)) return false
    localStorage.setItem(SEEN_KEY, JSON.stringify([...seen, key]))
    say(message, options)
    return true
}

// Olvida los consejos ya mostrados.
export function resetTips() {
    localStorage.removeItem(SEEN_KEY)
}

export function useBlobSay() {
    return { blob, say, sayOnce, hush, resetTips }
}

if (import.meta.env.DEV) {
    window.blobSay = say
    window.blobResetTips = resetTips
}