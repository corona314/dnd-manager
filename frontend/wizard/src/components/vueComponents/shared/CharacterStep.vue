<script setup>
    // Layout común de los pasos de la creación de personaje:
    //   cabecera fija  →  centro con scroll (75% de ancho)  →  pie fijo con los botones
    defineProps({
        error: String,   // mensaje de error: sale flotando bajo la cabecera, sin empujar nada
        backLabel: { type: String, default: 'Back' },
        continueLabel: { type: String, default: 'Continue Creation' },
        continueDisabled: Boolean,
        showContinue: { type: Boolean, default: true }
    })

    const emit = defineEmits(['back', 'continue'])
</script>

<template>
    <div class="character_step">
        <div v-if="$slots.header || error" class="character_step_header">
            <slot name="header" />
            <div v-if="error" class="character_step_error" role="alert">{{ error }}</div>
        </div>

        <!-- Zona fija opcional entre la cabecera y el centro (filtros, pestañas...) -->
        <div v-if="$slots.toolbar" class="character_step_toolbar">
            <slot name="toolbar" />
        </div>

        <div class="character_step_scroll">
            <div class="character_step_body">
                            <div v-blob-spot class="blob_spot_side"></div>

                <slot />
            </div>
        </div>

        <div class="character_step_footer">
            <!-- Por defecto: volver y continuar. Se puede sustituir con el slot "footer" -->
            <slot name="footer">
                <button class="general_button" @click="emit('back')">{{ backLabel }}</button>
                <button
                    v-if="showContinue"
                    class="general_button"
                    :disabled="continueDisabled"
                    @click="emit('continue')"
                >{{ continueLabel }}</button>
            </slot>
        </div>
    </div>
</template>

<style scoped>
    
    .character_step {
        height: 100%;
        display: flex;
        flex-direction: column;
        align-items: center;
        overflow: hidden;
        background-color: var(--background);
    }

    /* Cabecera de altura mínima fija, con línea inferior que la separa del contenido */
    .character_step_header {
        position: relative;
        z-index: 2;
        flex-shrink: 0;
        box-sizing: border-box;
        width: 100%;
        min-height: 64px;
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        padding: 10px 20px;
        border-bottom: 2px solid var(--border);
        background-color: var(--background);
    }

    /* El error flota sobre el contenido: no cambia la altura de la cabecera */
    .character_step_error {
        position: absolute;
        top: calc(100% + 8px);
        left: 50%;
        transform: translateX(-50%);
        max-width: 75%;
        padding: 6px 14px;
        border: 1px solid var(--danger-border);
        border-radius: 6px;
        background: var(--danger-bg);
        color: var(--text);
        font-size: 0.85rem;
        text-align: center;
    }

    .character_step_toolbar {
        flex-shrink: 0;
        width: 100%;
        display: flex;
        flex-direction: column;
        align-items: center;
    }

    /* El scroll vive aquí, entre la cabecera y el pie, no en el borde de la ventana */
    .character_step_scroll {
        flex: 1 1 auto;
        min-height: 0;
        width: 75%;
        overflow-y: auto;
        padding: 10px 0 20px;
    }

    /* stretch: los hijos ocupan el ancho de la columna (antes se encogían al contenido) */
    .character_step_body {
        display: flex;
        flex-direction: column;
        align-items: stretch;
        gap: 12px;
    }

    .character_step_footer {
        flex-shrink: 0;
        display: flex;
        justify-content: space-between;
        align-items: center;
        width: 75%;
        padding: 10px 20px;
        border-top: 2px solid var(--border);
        background-color: var(--background);
    }

    /* "Continue" desactivado: que se note que todavía no se puede pulsar */
    .character_step_footer .general_button:disabled {
        opacity: 0.45;
        cursor: not-allowed;
    }
</style>