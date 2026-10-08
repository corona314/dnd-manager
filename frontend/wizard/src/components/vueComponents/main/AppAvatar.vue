<script setup>
import { ref, computed, watch, nextTick, onMounted, onBeforeUnmount } from 'vue'
import { Blobatar } from '@blobatar/vue'
import { gaze } from 'blobatar/gaze'
import * as expressions from 'blobatar/expression'
import 'blobatar/motion.css'
import 'blobatar/gaze.css'
import { blob } from '@/composables/useBlobSay.js'



// speaks: solo el avatar del header debe reaccionar a los mensajes del blob
const props = defineProps({ name: String, speaks: Boolean })

const MOODS = ['happy', 'wink', 'surprised', 'smug', 'love', 'sleepy', 'unsure', 'shy', 'thinking', 'mad', 'sad'];

const current = ref(null)

// Pose neutra explícita (si la librería la exporta; si no, queda undefined y es la de por defecto)
const idle = expressions.idle

// Mientras habla, su expresión manda sobre los cambios de humor aleatorios
const expression = computed(() => {
  if (props.speaks && blob.message && blob.mood) {
    return expressions[blob.mood] ?? idle
  }
  return current.value ? expressions[current.value] : idle
})

const wrapper = ref(null)
let driver
let timer
let raf
let lastX = 0
let lastY = 0

const rand = (min, max) => min + Math.random() * (max - min)

function scheduleNext() {
  timer = setTimeout(() => {
    current.value = MOODS[Math.floor(Math.random() * MOODS.length)]
    timer = setTimeout(() => {
      current.value = null
      scheduleNext()
    }, rand(1500, 3000))
  }, rand(20000, 40000))
}

watch(() => blob.message, () => {
  if (!props.speaks) return
  clearTimeout(timer)
  current.value = null
  scheduleNext()
})

function watchPosition() {
  const rect = wrapper.value?.getBoundingClientRect()
  if (rect && (rect.left !== lastX || rect.top !== lastY)) {
    lastX = rect.left
    lastY = rect.top
    window.dispatchEvent(new Event('scroll'))
  }
  raf = requestAnimationFrame(watchPosition)
}

async function start() {
  await nextTick()
  const svg = wrapper.value?.querySelector('svg')
  if (svg) driver = gaze(svg, { target: 'pointer' })
  scheduleNext()
  raf = requestAnimationFrame(watchPosition)
}

function stop() {
  driver?.stop()
  driver = null
  clearTimeout(timer)
  cancelAnimationFrame(raf)
}

onMounted(start)
onBeforeUnmount(stop)
</script>

<template>
  <span ref="wrapper" class="avatar_wrapper">
    <Blobatar :name="name" :expression="expression" animate="hover" />
  </span>
</template>

<style scoped>
.avatar_wrapper {
  display: block;
  width: 100%;
  height: 100%;
}

.avatar_wrapper :deep(svg) {
  --mo-track-travel: 10px;
  width: 100%;
  height: 100%;
  display: block;
}
</style>