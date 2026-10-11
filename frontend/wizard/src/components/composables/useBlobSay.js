import { reactive } from 'vue'

// Estado compartido: lo leen los bocadillos, el escenario y el avatar.
//   id: cambia con cada mensaje (reinicia la barra de progreso)
//   duration: ms que durará el mensaje (0 = espera a que el usuario lo cierre)
//   stage: true = lo dice el blob grande (BlobStage); false = bocadillo del header
//   away: true mientras el blob está fuera del header (lo gestiona BlobStage)

export const blob = reactive({ message: '', mood: null, id: 0, duration: 0, stage: false, away: false })

// Tiempo de entrada al escenario, más un pequeño margen.
const STAGE_ENTER_MS = 1100

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
//   say('Hi')                                      - mensaje en el header
//   say('Hi', { mood: 'wink' })                    - mensaje con expresión
//   say('Hi', { stage: true })                     - mensaje en el escenario
//   say('Hi', { duration: 0 })                     - espera a que se cierre
//   say('Hi', { stage: true, duration: 5000 })     - duración total de 5 s
export function say(message, { mood = 'happy', duration, stage = false } = {}) {
    clearTimeout(timer)

    const ms = duration ?? Math.max(4000, message.length * 100)
    const totalDuration = ms > 0
        ? ms + (stage && !blob.away ? STAGE_ENTER_MS : 0)
        : 0

    blob.id++
    blob.message = message
    blob.mood = mood
    blob.stage = stage
    blob.duration = totalDuration

    if (totalDuration > 0) {
        timer = setTimeout(hush, totalDuration)
    }
}

// Como say(), pero solo la primera vez para esa clave.
export function sayOnce(key, message, options) {
    const seen = readSeen()

    if (seen.includes(key)) return false

    localStorage.setItem(SEEN_KEY, JSON.stringify([...seen, key]))
    say(message, options)

    return true
}

// Callarlo.
export function hush() {
    clearTimeout(timer)

    blob.message = ''
    blob.mood = null
    blob.duration = 0
    blob.stage = false
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
