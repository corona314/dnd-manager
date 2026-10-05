import { ref } from 'vue'

const API_BASE = 'http://localhost:8080/api'

// path: ruta del backend, p. ej. '/spells', o una función que la devuelve
//       (para rutas que cambian, como las pestañas de objetos): () => '/items/armor'
// getToken: () => props.token
// onLoaded: se ejecuta cuando el detalle ya está cargado (opcional)
export function useCardExpansion(path, getToken, { onLoaded } = {}) {
    const resolvePath = typeof path === 'function' ? path : () => path

    const expanded_id = ref(null)
    const expanded = ref(null)
    const expanded_loading = ref(false)

    function collapse() {
        expanded.value = null
        expanded_id.value = null
    }

    async function expand(entity) {
        if (expanded_id.value === entity.id) {
            collapse()
            return
        }
        expanded_loading.value = true
        try {
            const res = await fetch(`${API_BASE}${resolvePath()}/${entity.id}`, {
                headers: { Authorization: `Bearer ${getToken()}` }
            })
            expanded.value = await res.json()
            expanded_id.value = entity.id
            onLoaded?.(expanded.value)
        } catch (e) {
            console.error(e)
        } finally {
            expanded_loading.value = false
        }
    }

    return { expanded_id, expanded, expanded_loading, expand, collapse }
}