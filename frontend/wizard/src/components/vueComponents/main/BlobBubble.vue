<script setup>
    import { blob, hush } from '@/composables/useBlobSay.js'
</script>

<template>
    <Transition name="bubble">
        <div
            v-if="blob.message"
            class="blob_bubble"
            role="status"
            aria-live="polite"
            title="Click to dismiss"
            @click="hush"
        >
            {{ blob.message }}
        </div>
    </Transition>
</template>

<style scoped>
    .blob_bubble {
        position: absolute;
        top: calc(100% + 12px);
        right: 0;
        z-index: 2;
        width: max-content;
        max-width: 260px;
        padding: 8px 12px;
        border: 1px solid var(--border);
        border-radius: 12px;
        background: var(--surface);
        color: var(--text);
        box-shadow: 0 4px 12px var(--shadow);
        font-size: 0.85rem;
        line-height: 1.4;
        cursor: pointer;
        user-select: none;
    }

    /* Colita que apunta al avatar */
    .blob_bubble::before {
        content: '';
        position: absolute;
        top: -6px;
        right: 14px;
        width: 10px;
        height: 10px;
        background: var(--surface);
        border-top: 1px solid var(--border);
        border-left: 1px solid var(--border);
        transform: rotate(45deg);
    }

    .bubble-enter-active,
    .bubble-leave-active {
        transition: opacity 0.2s ease, transform 0.2s ease;
    }

    .bubble-enter-from,
    .bubble-leave-to {
        opacity: 0;
        transform: translateY(-6px);
    }
</style>