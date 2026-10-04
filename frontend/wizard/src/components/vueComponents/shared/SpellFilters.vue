<script setup>
    import Slider from 'primevue/slider'
    import RitualIcon from '@/icons/RitualIcon.vue'
    import ConcentrationIcon from '@/icons/ConcentrationIcon.vue'
    import NoRitualIcon from '@/icons/NoRitualIcon.vue'
    import NoConcentrationIcon from '@/icons/NoConcentrationIcon.vue'
    import VocalIcon from '@/icons/VocalIcon.vue'
    import NoVocalIcon from '@/icons/NoVocalIcon.vue'
    import SomaticIcon from '@/icons/SomaticIcon.vue'
    import NoSomaticIcon from '@/icons/NoSomaticIcon.vue'
    import MaterialIcon from '@/icons/MaterialIcon.vue'
    import NoMaterialIcon from '@/icons/NoMaterialIcon.vue'
    import SearchIcon from '@/icons/SearchIcon.vue'
    import { ref } from 'vue'
    import { Components, Schools, SortLabels } from '@/composables/spellFilters.js'
    import { usingIcons } from '@/composables/usePreferences.js'
    // El padre es dueño del objeto de filtros (reactive) y se edita aquí dentro.
    const filters = defineModel({ required: true })
    // Abierto/cerrado: opcional. Si el padre no lo enlaza, funciona igualmente.
    const open = defineModel('open', { default: true })

    defineProps({
        minLevel: { type: Number, default: 0 },
        maxLevel: { type: Number, default: 9 },
        showLevel: { type: Boolean, default: true },
        showSearchButton: { type: Boolean, default: false }
    })

    // 'apply' se emite cuando el usuario cambia algo que el padre quizá quiera
    // procesar (el compendio lo usa para volver a pedir datos al servidor).
    const emit = defineEmits(['apply'])

    const sortOpen = ref(false)
    const schoolOpen = ref(false)

    const next = v => (v === null ? true : v === true ? false : null)
    const prev = v => (v === null ? false : v === false ? true : null)
    
    const ComponentIcons = {
        V: { on: VocalIcon, off: NoVocalIcon },
        S: { on: SomaticIcon, off: NoSomaticIcon },
        M: { on: MaterialIcon, off: NoMaterialIcon }
    }
    
    function cycleComponent(c) {
        filters.value.components[c] = next(filters.value.components[c])
        emit('apply')
    }
    function previousComponent(c) {
        filters.value.components[c] = prev(filters.value.components[c])
        emit('apply')
    }
    function cycleRitual() {
        filters.value.ritual = next(filters.value.ritual)
        emit('apply')
    }
    function previousRitual() {
        filters.value.ritual = prev(filters.value.ritual)
        emit('apply')
    }
    function cycleConcentration() {
        filters.value.concentration = next(filters.value.concentration)
        emit('apply')
    }
    function previousConcentration() {
        filters.value.concentration = prev(filters.value.concentration)
        emit('apply')
    }

    function toggleSchool(id) {
        const list = filters.value.schools
        const i = list.indexOf(id)
        if (i === -1) list.push(id)
        else list.splice(i, 1)
        emit('apply')
    }

    function setSortField(field) {
        filters.value.sortField = field
        sortOpen.value = false
        emit('apply')
    }
    function toggleSortDirection() {
        filters.value.sortDirection = filters.value.sortDirection === 'asc' ? 'desc' : 'asc'
        emit('apply')
    }

    // El slider es de un solo sentido (:model-value), así que aquí dentro
    // filters.level todavía tiene el valor anterior y se puede comparar con él.
    function sliderOrder(value) {
        const [newMin, newMax] = value
        const [oldMin, oldMax] = filters.value.level

        let min = newMin
        let max = newMax

        if (newMin !== oldMin) {
            min = Math.min(newMin, oldMax)
            max = oldMax
        } else if (newMax !== oldMax) {
            max = Math.max(newMax, oldMin)
            min = oldMin
        }

        filters.value.level = [min, max]
    }

    function toggleOpen() {
        open.value = !open.value
    }
</script>

<template>
    <div class="compendium_filters" :class="{ 'compendium_filters--open': open === true, 'compendium_filters--closed': open === false }">
        <div class="name">
            <input class="name_input" type="text" placeholder="Search spell..." v-model="filters.name" @keyup.enter="emit('apply')" />
            <button v-if="showSearchButton" class="search_button" @click="emit('apply')"><SearchIcon /></button>
        </div>

        <div class="level" v-if="showLevel">
            <span class="level_label">
                {{ filters.level[0] === 0 ? 'Cantrip (0)' : filters.level[0] }}
                --
                {{ filters.level[1] === 0 ? 'Cantrip (0)' : filters.level[1] }}
            </span>
            <Slider
                class="level_slider"
                :model-value="filters.level"
                :min="minLevel"
                :max="maxLevel"
                :step="1"
                range
                @update:modelValue="sliderOrder"
                @slideend="emit('apply')"
            />
        </div>

        <div class="sort_filter">
            <span class="sort_title">Order by:</span>

            <div class="sort_dropdown">
                <div class="dropdown_btn" @click="sortOpen = !sortOpen">
                    {{ SortLabels[filters.sortField] }}
                    <i>-</i>
                </div>

                <div v-if="sortOpen" class="dropdown_menu">
                    <div
                        v-for="(label, field) in SortLabels"
                        :key="field"
                        class="dropdown_option"
                        :class="{ selected: filters.sortField === field }"
                        @click="setSortField(field)"
                    >
                        {{ label }}
                    </div>
                </div>
            </div>

            <button
                class="sort_direction"
                @click="toggleSortDirection"
                :title="filters.sortDirection === 'asc' ? 'Ascending' : 'Descending'"
            >
                {{ filters.sortDirection === 'asc' ? '▲' : '▼' }}
            </button>
        </div>

        <div class="components_group">
            <span
                v-for="comp in Components"
                :key="comp"
                class="tristate"
                :title = "comp === 'V' ? 'Verbal' : comp === 'S' ? 'Somatic' : 'Material'"
                @click="cycleComponent(comp)"
                @contextmenu.prevent="previousComponent(comp)"
                :class="{
                    'tristate--active': filters.components[comp] === true,
                    'tristate--inactive': filters.components[comp] === false
                }"
            >
                <component
                    v-if="usingIcons"
                    :is="filters.components[comp] === false ? ComponentIcons[comp].off : ComponentIcons[comp].on"
                />
                <template v-else>{{ comp }}</template>
            </span>
        </div>

        <div class="filter_group special_group">
          <span
              class="tristate tristate_ritual"
              @click="cycleRitual"
              @contextmenu.prevent="previousRitual"
              :class="{
                  'tristate--active': filters.ritual === true,
                  'tristate--inactive': filters.ritual === false
              }"
              title="Ritual"
          >
              <template v-if="usingIcons">
                  <NoRitualIcon v-if="filters.ritual === false" />
                  <RitualIcon v-else />
              </template>
              <template v-else>R</template>
          </span>

          <span
              class="tristate tristate_concentration"
              @click="cycleConcentration"
              @contextmenu.prevent="previousConcentration"
              :class="{
                  'tristate--active': filters.concentration === true,
                  'tristate--inactive': filters.concentration === false
              }"
              title="Concentration"
          >
              <template v-if="usingIcons">
                  <NoConcentrationIcon v-if="filters.concentration === false" />
                  <ConcentrationIcon v-else />
              </template>
              <template v-else>C</template>
          </span>
        </div>

        <div class="school_filter_group">
            <div class="school_filter">
                <div class="dropdown_btn" @click="schoolOpen = !schoolOpen">
                    Schools <i>-</i>
                </div>

                <div v-if="schoolOpen" class="dropdown_menu">
                    <div
                        v-for="(name, id) in Schools"
                        :key="id"
                        class="dropdown_option"
                        :class="{ selected: filters.schools.includes(+id) }"
                        @click="toggleSchool(+id)"
                    >
                        {{ name }}
                    </div>
                </div>
            </div>
            <div v-if="filters.schools.length" class="school_chips">
                <span v-for="id in filters.schools" :key="id" class="school_chip">
                    {{ Schools[id] }}
                    <span class="school_chip_remove" @click.stop="toggleSchool(id)">✕</span>
                </span>
            </div>
        </div>

        <button class="compendium_filters_toggle" @click="toggleOpen">{{ open === true ? '▲' : '▼' }}</button>
    </div>
</template>