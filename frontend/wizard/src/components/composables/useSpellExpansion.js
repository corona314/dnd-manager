import { ref } from 'vue'

const API_BASE = 'http://localhost:8080/api'

// getToken es una función para leer siempre el token actual: () => props.token
export function useSpellExpansion(getToken) {
    const expanded_id = ref(null)
    const expanded_spell = ref(null)
    const expanded_loading = ref(false)
    const selected_upcast_level = ref(null)

    async function expandSpell(spell) {
        if (expanded_id.value === spell.id) {
            expanded_spell.value = null
            expanded_id.value = null
            return
        }
        expanded_loading.value = true
        try {
            const res = await fetch(`${API_BASE}/spells/${spell.id}`, {
                headers: { Authorization: `Bearer ${getToken()}` }
            })
            expanded_spell.value = await res.json()
            expanded_id.value = spell.id
            selected_upcast_level.value = expanded_spell.value.level + 1
        } catch (e) {
            console.error(e)
        } finally {
            expanded_loading.value = false
        }
    }

    function selectUpcastLevel(level) {
        selected_upcast_level.value = level
    }

    return { expanded_id, expanded_spell, expanded_loading, selected_upcast_level, expandSpell, selectUpcastLevel }
}