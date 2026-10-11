import { registerSpot, unregisterSpot } from '@/composables/useBlobSpots.js'

// Uso: <div v-blob-spot class="blob_spot"></div>
export default {
    mounted(el) {
        registerSpot(el)
    },
    unmounted(el) {
        unregisterSpot(el)
    }
}