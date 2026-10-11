<script setup>
    import { onMounted, onBeforeUnmount } from 'vue'
    import { blob, hush } from '@/composables/useBlobSay.js'
    import '@/styles/blob/blobMessage.css'
    // Dónde apunta la colita: 'up' (header), 'left' o 'right' (escenario)
    defineProps({ tail: { type: String, default: 'up' } })

    // Escape cierra el mensaje (el componente solo existe mientras hay mensaje)
    function onKey(e) {
        if (e.key === 'Escape') hush()
    }

    onMounted(() => window.addEventListener('keydown', onKey))
    onBeforeUnmount(() => window.removeEventListener('keydown', onKey))
</script>

<template>
    <div
        class="blob_bubble"
        :class="[`blob_bubble--tail-${tail}`, { 'blob_bubble--waiting': blob.duration === 0 }]"
        role="status"
        aria-live="polite"
        @click="hush"
    >
        <span class="blob_text">{{ blob.message }}</span>

        <!-- Mensaje que espera: botón explícito -->
        <button v-if="blob.duration === 0" class="blob_gotit" @click.stop="hush">Got it</button>

        <!-- Mensaje que se cierra solo: barra que se vacía -->
        <div v-else :key="blob.id" class="blob_progress">
            <div class="blob_progress_bar" :style="{ animationDuration: blob.duration + 'ms' }"></div>
        </div>
    </div>
</template>