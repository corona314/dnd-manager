import { ref } from 'vue'
import { useCardExpansion } from './useCardExpansion.js'

// Misma interfaz que antes: AppSpells y AppCharacterSpells1 no cambian.
export function useSpellExpansion(getToken) {
    const selected_upcast_level = ref(null)

    const { expanded_id, expanded, expanded_loading, expand } = useCardExpansion('/spells', getToken, {
        onLoaded: (spell) => { selected_upcast_level.value = spell.level + 1 }
    })

    function selectUpcastLevel(level) {
        selected_upcast_level.value = level
    }

    return {
        expanded_id,
        expanded_spell: expanded,
        expanded_loading,
        selected_upcast_level,
        expandSpell: expand,
        selectUpcastLevel
    }
}