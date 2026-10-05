import { marked } from 'marked'

// Convierte la descripción (markdown, con tablas a veces mal formadas) en HTML.
export function renderDescription(text) {
    if (!text) return ''
    const fixed = text
        .replace(/\|\s*\|/g, '|\n|')
        .replace(/(Table:[^\n|]+)\|/, '$1\n|')
    return marked(fixed)
}