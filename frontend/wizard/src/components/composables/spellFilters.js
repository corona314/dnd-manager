export const Components = ['V', 'S', 'M']

export const Schools = {
    1: 'Abjuration',
    2: 'Conjuration',
    3: 'Divination',
    4: 'Enchantment',
    5: 'Evocation',
    6: 'Illusion',
    7: 'Necromancy',
    8: 'Transmutation'
}

export const SortLabels = { name: 'Name', level: 'Level', school: 'School' }

// Estado inicial de los filtros. Usar siempre con reactive():
//   const filters = reactive(createSpellFilters({ level: [0, 9] }))
export function createSpellFilters({ level = [0, 9] } = {}) {
    return {
        name: '',
        level: [...level],
        schools: [],
        components: { V: null, S: null, M: null },
        ritual: null,
        concentration: null,
        sortField: 'name',
        sortDirection: 'asc'
    }
}