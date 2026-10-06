import { ref } from 'vue'

const API_BASE = 'http://localhost:8080/api'

// path: ruta del backend, p. ej. '/spells', o una función que la devuelve
//       (para rutas que cambian, como las pestañas de objetos): () => '/items/armor'
// getToken: () => props.token
// multiple: true = varias tarjetas abiertas a la vez; false = solo una
export function useCardExpansion(path, getToken, { multiple = false } = {}) {
    const resolvePath = typeof path === 'function' ? path : () => path

    const open_ids = ref([])      // ids de las tarjetas abiertas
    const details = ref({})       // id -> detalle ya cargado (caché)
    const loading_ids = ref([])   // ids cuyo detalle se está cargando
    let generation = 0            // para ignorar respuestas de antes de un reset()

    const isOpen = (id) => open_ids.value.includes(id)
    const isLoading = (id) => loading_ids.value.includes(id)

    function close(id) {
        open_ids.value = open_ids.value.filter(i => i !== id)
    }

    async function load(id) {
        if (details.value[id] || isLoading(id)) return
        const gen = generation
        loading_ids.value.push(id)
        try {
            const res = await fetch(`${API_BASE}${resolvePath()}/${id}`, {
                headers: { Authorization: `Bearer ${getToken()}` }
            })
            if (!res.ok) throw new Error(`Error ${res.status}`)
            const data = await res.json()
            if (gen === generation) details.value[id] = data
        } catch (e) {
            console.error(e)
            if (gen === generation) close(id)
        } finally {
            loading_ids.value = loading_ids.value.filter(i => i !== id)
        }
    }

    function toggle(entity) {
        const id = entity.id
        if (isOpen(id)) {
            close(id)
            return
        }
        open_ids.value = multiple ? [...open_ids.value, id] : [id]
        load(id)
    }

    // Cierra todas (conserva los detalles ya cargados)
    function collapseAll() {
        open_ids.value = []
    }

    // Cierra todas y olvida los detalles (usar al cambiar de pestaña/lista)
    function reset() {
        generation++
        open_ids.value = []
        details.value = {}
        loading_ids.value = []
    }

    return { open_ids, details, isOpen, isLoading, toggle, collapseAll, reset }
}