import { shallowRef } from 'vue'

// Registro de "sitios": elementos de la página donde el blob puede ponerse
// cuando sale al escenario. Se rellena con la directiva v-blob-spot.
// Si hay varios, manda el último que se montó (el de la pantalla actual).
const spots = shallowRef([])

export function registerSpot(el) {
    spots.value = [...spots.value.filter(e => e !== el), el]
}

export function unregisterSpot(el) {
    spots.value = spots.value.filter(e => e !== el)
}

// Devuelve el sitio activo, o null si no hay ninguno.
// Ignora los que no ocupan espacio (display: none), para caer en la esquina.
export function currentSpot() {
    const list = spots.value
    for (let i = list.length - 1; i >= 0; i--) {
        const el = list[i]
        if (!el.isConnected) continue
        const r = el.getBoundingClientRect()
        if (r.width > 0 && r.height > 0) return el
    }
    return null
}