import { reactive } from 'vue'

// Estado compartido: lo leen el bocadillo (BlobBubble) y el avatar (AppAvatar).
export const blob = reactive({ message: '', mood: null })

const SEEN_KEY = 'dnd_blob_seen'
let timer

function readSeen() {
    try {
        return JSON.parse(localStorage.getItem(SEEN_KEY) || '[]')
    } catch {
        return []
    }
}

// Hace que el blob diga algo.
//   say('Hi')                                  → dura según la longitud del texto
//   say('Hi', { mood: 'wink' })                → con otra expresión (por defecto 'happy')
//   say('Hi', { duration: 6000 })              → duración en ms
//   say('Hi', { duration: 0 })                 → se queda hasta que se pulse el bocadillo
export function say(message, { mood = 'happy', duration } = {}) {
    clearTimeout(timer)
    blob.message = message
    blob.mood = mood

    const ms = duration ?? Math.max(4000, message.length * 60)
    if (ms > 0) timer = setTimeout(hush, ms)
}

// Lo calla.
export function hush() {
    clearTimeout(timer)
    blob.message = ''
    blob.mood = null
}

// Como say(), pero solo la primera vez para esa clave (se recuerda en localStorage).
// Devuelve true si lo dijo y false si ya lo había dicho antes.
export function sayOnce(key, message, options) {
    const seen = readSeen()
    if (seen.includes(key)) return false
    localStorage.setItem(SEEN_KEY, JSON.stringify([...seen, key]))
    say(message, options)
    return true
}

// Olvida los consejos ya mostrados (útil para un botón en Configuración).
export function resetTips() {
    localStorage.removeItem(SEEN_KEY)
}

export function useBlobSay() {
    return { blob, say, sayOnce, hush, resetTips }
}

// Para probarlo desde la consola del navegador:  blobSay('Hi', { mood: 'wink' })
if (import.meta.env.DEV) window.blobSay = say