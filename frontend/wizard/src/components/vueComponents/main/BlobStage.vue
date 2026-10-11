<script setup>
    import { ref, computed, watch, nextTick, onBeforeUnmount } from 'vue'
    import AppAvatar from './AppAvatar.vue'
    import BlobMessage from './BlobMessage.vue'
    import { blob } from '@/composables/useBlobSay.js'
    import { currentSpot } from '@/composables/useBlobSpots.js'
    import '@/styles/blob/blobStage.css'

    defineProps({ name: String })

    const SIZE = 140                         // tamaño del blob en el escenario
    const MARGIN = 24                        // separación de la esquina por defecto
    const HEADER_SELECTOR = '.header_avatar_btn'   // de dónde "salta"

    // ---- Ajustes de la animación ----
    const ENTER_MS = 900      // duración de la caída (si la cambias, sube STAGE_ENTER_MS en useBlobSay.js)
    const LEAVE_MS = 380      // duración de la vuelta al header
    const OVERSHOOT = 1.3     // cuánto crece al llegar antes de asentarse (1 = nada)
    const BOUNCE_PX = 30      // altura del bote al aterrizar (0 = sin bote)
    const LAND_AT = 0.68      // en qué punto de la caída toca "suelo" (0 a 1)

    // idle → entering (cayendo) → shown (quieto, con bocadillo) → leaving (volviendo) → idle
    const phase = ref('idle')
    const target = ref({ x: 0, y: 0, size: SIZE })
    const figure = ref(null)
    const shadow = ref(null)

    let anim = null
    let shadowAnim = null
    let raf = 0

    const wantsStage = computed(() => !!blob.message && blob.stage)
    const bubbleSide = computed(() =>
        target.value.x + target.value.size / 2 > window.innerWidth / 2 ? 'left' : 'right'
    )
    const stageStyle = computed(() => ({
        left: `${target.value.x}px`,
        top: `${target.value.y}px`,
        width: `${target.value.size}px`,
        height: `${target.value.size}px`
    }))

    const reduceMotion = () => window.matchMedia('(prefers-reduced-motion: reduce)').matches

    // ---- Dónde ponerse: en el sitio de la página (v-blob-spot) o, si no hay, en la esquina ----
    function measure() {
        const spot = currentSpot()
        let next
        if (spot) {
            const r = spot.getBoundingClientRect()
            const size = Math.max(60, Math.min(SIZE, r.width, r.height))
            next = { x: r.left + (r.width - size) / 2, y: r.top + (r.height - size) / 2, size }
        } else {
            next = {
                x: window.innerWidth - SIZE - MARGIN,
                y: window.innerHeight - SIZE - MARGIN,
                size: SIZE
            }
        }
        const t = target.value
        if (Math.abs(next.x - t.x) > 0.5 || Math.abs(next.y - t.y) > 0.5 || next.size !== t.size) {
            target.value = next
        }
    }

    // Mientras está en pantalla, vuelve a medir (scroll, resize, cambio de pantalla...)
    function track() {
        measure()
        raf = requestAnimationFrame(track)
    }

    function headerRect() {
        const r = document.querySelector(HEADER_SELECTOR)?.getBoundingClientRect()
        return r
            ? { cx: r.left + r.width / 2, cy: r.top + r.height / 2, size: r.width }
            : { cx: window.innerWidth - 40, cy: 30, size: 40 }
    }

    // Desplazamiento y escala para ir del destino al header
    function offsetToHeader() {
        const from = headerRect()
        const t = target.value
        return {
            dx: from.cx - (t.x + t.size / 2),
            dy: from.cy - (t.y + t.size / 2),
            scale: from.size / t.size
        }
    }

    // ---- Entrada: cae desde el header creciendo, rebota y se queda ----
    function playEnter() {
        const el = figure.value
        if (!el || phase.value !== 'entering') return
        const { dx, dy, scale } = offsetToHeader()

        // Punto medio entre que toca "suelo" y se asienta: ahí está el punto alto del bote
        const bounceAt = LAND_AT + (1 - LAND_AT) / 2

        anim = el.animate([
            // 1. Caída desde el header: la curva hace que acelere (más rápida al final)
            { transform: `translate(${dx}px, ${dy}px) scale(${scale})`, offset: 0, easing: 'cubic-bezier(.5, 0, .9, .6)' },
            // 2. Llega al destino pasándose de tamaño
            { transform: `translate(0px, 0px) scale(${OVERSHOOT})`, offset: LAND_AT, easing: 'ease-out' },
            // 3. Bote hacia arriba
            { transform: `translate(0px, -${BOUNCE_PX}px) scale(0.96)`, offset: bounceAt, easing: 'ease-in' },
            // 4. Reposo
            { transform: 'translate(0px, 0px) scale(1)', offset: 1 }
        ], { duration: ENTER_MS })

        // Sombra en el suelo: crece a medida que se acerca (da sensación de profundidad)
        shadowAnim = shadow.value?.animate([
            { opacity: 0, transform: 'scale(0.2)' },
            { opacity: 0.15, transform: 'scale(0.6)', offset: LAND_AT },
            { opacity: 0.35, transform: 'scale(1)' }
        ], { duration: ENTER_MS, fill: 'forwards' })

        anim.onfinish = () => {
            anim = null
            if (phase.value === 'entering') phase.value = 'shown'
        }
    }

    async function enter() {
        anim?.cancel()
        anim = null
        shadowAnim?.cancel()

        const alreadyAway = blob.away          // ya estaba fuera (p. ej. mensaje nuevo mientras volvía)
        const skipFall = alreadyAway || reduceMotion()

        measure()
        cancelAnimationFrame(raf)
        raf = requestAnimationFrame(track)

        phase.value = skipFall ? 'shown' : 'entering'
        blob.away = true

        await nextTick()
        if (!skipFall) playEnter()
    }

    // ---- Salida: encoge y vuelve al avatar del header ----
    function finish() {
        anim = null
        phase.value = 'idle'
        blob.away = false
        cancelAnimationFrame(raf)
    }

    async function leave() {
        if (phase.value === 'idle') return
        anim?.cancel()
        anim = null
        shadowAnim?.cancel()
        phase.value = 'leaving'

        await nextTick()
        const el = figure.value
        if (!el || reduceMotion()) {
            finish()
            return
        }

        const { dx, dy, scale } = offsetToHeader()
        anim = el.animate([
            { transform: 'translate(0px, 0px) scale(1)', easing: 'ease-in' },
            { transform: `translate(${dx}px, ${dy}px) scale(${scale})` }
        ], { duration: LEAVE_MS, fill: 'forwards' })
        anim.onfinish = finish
    }

    watch(wantsStage, (on) => (on ? enter() : leave()), {
        immediate: true
    })

    onBeforeUnmount(() => {
        anim?.cancel()
        cancelAnimationFrame(raf)
        blob.away = false
    })
</script>

<template>
    <div v-if="phase !== 'idle'" class="blob_stage" :class="`blob_stage--${bubbleSide}`" :style="stageStyle">
        <div ref="shadow" class="blob_stage_shadow"></div>

        <div ref="figure" class="blob_stage_figure">
            <div class="blob_stage_bob">
                <AppAvatar :name="name" speaks animate="always" />
            </div>
        </div>

        <Transition name="stage-bubble">
            <div v-if="phase === 'shown' && blob.message" class="blob_stage_bubble">
                <BlobMessage :tail="bubbleSide === 'left' ? 'right' : 'left'" />
            </div>
        </Transition>
    </div>
</template>
