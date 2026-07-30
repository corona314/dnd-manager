function preventDoubleSelect(event) {
    if (event.detail > 1) {
        event.preventDefault()
    }
}

export default {
    mounted(el) {
        el.addEventListener('mousedown', preventDoubleSelect)
    },
    unmounted(el) {
        el.removeEventListener('mousedown', preventDoubleSelect)
    }
}