<script setup>
    import { ref, computed, onMounted, onBeforeUnmount } from 'vue'

    // Barra "elige una clase / especie / trasfondo": desplegable + botón de elegir.
    // Va dentro del slot "toolbar" de CharacterStep. Las opciones necesitan { id, name }.
    const props = defineProps({
        noun: { type: String, required: true },       // 'Class', 'Species', 'Background'...
        options: { type: Array, default: () => [] },
        loading: Boolean,
        viewedId: [Number, String],                   // el que se está mirando en el desplegable
        viewedName: String,
        currentId: [Number, String],                  // el que ya tiene guardado el personaje
        saving: Boolean,
        pickDisabled: Boolean,                        // condición extra para bloquear el botón
        hint: String                                  // aviso junto al botón (opcional)
    })

    const emit = defineEmits(['view', 'pick'])

    const open = ref(false)
    const root = ref(null)

    const hasViewed = computed(() => props.viewedId !== null && props.viewedId !== undefined)
    const isCurrent = computed(() => hasViewed.value && props.viewedId === props.currentId)
    const buttonLabel = computed(() => {
        if (props.saving) return 'Saving...'
        if (isCurrent.value) return `Current ${props.noun}`
        return `Pick ${props.viewedName ?? ''}`
    })

    function choose(option) {
        open.value = false
        emit('view', option)
    }

    // Un clic fuera cierra el menú
    function onOutsideClick(e) {
        if (open.value && root.value && !root.value.contains(e.target)) open.value = false
    }

    onMounted(() => document.addEventListener('click', onOutsideClick))
    onBeforeUnmount(() => document.removeEventListener('click', onOutsideClick))
</script>

<template>
    <div ref="root" class="pick_bar">
        <span class="pick_label">Pick a {{ noun }}</span>

        <div class="pick_filter">
            <div class="pick_dropdown_btn" @click="open = !open">
                {{ viewedName ?? `Select a ${noun}` }} <i>▾</i>
            </div>

            <div v-if="open" class="pick_dropdown_menu">
                <div v-if="loading" class="pick_loading">Loading...</div>
                <template v-else>
                    <div
                        v-for="option in options"
                        :key="option.id"
                        class="pick_option"
                        :class="{ selected: option.id === currentId }"
                        @click="choose(option)"
                    >
                        {{ option.name }}
                    </div>
                </template>
            </div>
        </div>

        <button
            v-if="hasViewed"
            class="general_button pick_btn"
            :disabled="saving || isCurrent || pickDisabled"
            @click="emit('pick')"
        >
            {{ buttonLabel }}
        </button>

        <span v-if="hasViewed && hint" class="pick_hint">{{ hint }}</span>
    </div>
</template>

<style scoped>

    .pick_bar {
        display: flex;
        align-items: center;
        justify-content: center;
        flex-wrap: wrap;
        gap: 8px 16px;
        width: 100%;
        box-sizing: border-box;
        padding: 12px 20px;
        border-bottom: 1px solid var(--separator-lines);
        background-color: var(--background);
    }

    .pick_label {
        font-weight: 700;
    }

    .pick_hint {
        color: var(--text-secondary);
        font-size: 0.8rem;
    }

    /* El menú flota encima del contenido en vez de empujarlo */
    .pick_filter {
        position: relative;
    }

    .pick_dropdown_btn {
        display: flex;
        align-items: center;
        justify-content: space-between;
        gap: 12px;
        min-width: 200px;
        padding: 6px 12px;
        border: 1px solid var(--border);
        border-radius: 6px;
        background: var(--default);
        cursor: pointer;
        user-select: none;
    }

    .pick_dropdown_menu {
        position: absolute;
        top: 100%;
        left: 0;
        z-index: 10;
        min-width: 100%;
        max-height: 260px;
        overflow-y: auto;
        padding: 5px;
        border: 1px solid var(--border);
        border-radius: 4px;
        background: var(--default);
    }

    .pick_loading {
        padding: 4px 8px;
        color: var(--text-secondary);
    }

    .pick_option {
        padding: 4px 8px;
        border-radius: 4px;
        cursor: pointer;
        user-select: none;
    }

    .pick_option:hover {
        background: var(--surface-hover-strong);
    }

    .pick_option.selected {
        background: var(--accent-bg);
        outline: 1px solid var(--accent-border);
    }

    .pick_btn {
        padding: 8px 20px;
        border-color: var(--accent-strong-border);
        background: var(--accent-strong);
        color: var(--text-on-accent);
        font-weight: 700;
    }

    .pick_btn:disabled {
        opacity: 0.45;
        cursor: not-allowed;
    }
</style>