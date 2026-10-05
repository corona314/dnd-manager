<script setup>
    import '@/styles/appCompendium.css'

    defineProps({
        expanded: Boolean,
        selected: Boolean,
        selectable: Boolean,      // muestra el checkbox por defecto
        selectDisabled: Boolean
    })

    const emit = defineEmits(['toggle', 'toggle-select'])
</script>

<template>
    <div
        class="compendium_card"
        :class="{ 'compendium_card--expanded': expanded, 'compendium_card--selected': selected }"
        v-no-double-select
        @click="emit('toggle')"
    >
        <div class="compendium_card_header">
            <!-- Slot "select": por defecto un checkbox; una tienda puede poner aquí un contador -->
            <slot name="select">
                <label v-if="selectable" class="card_select" @click.stop>
                    <input
                        type="checkbox"
                        :checked="selected"
                        :disabled="selectDisabled"
                        :aria-label="selected ? 'Deselect' : 'Select'"
                        @change="emit('toggle-select')"
                    />
                </label>
            </slot>

            <div class="compendium_card_base">
                <slot name="base" />
            </div>
        </div>

        <div v-if="expanded" class="compendium_card_expanded" @click.stop>
            <slot name="expanded" />
        </div>
    </div>
</template>