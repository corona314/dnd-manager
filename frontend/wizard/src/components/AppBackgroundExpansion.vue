<script setup>
    import './styles/appBackgroundExpansion.css'
    import { ref, onMounted} from 'vue'
    import { marked } from 'marked'
    const props = defineProps({ token: String, backgroundId: Number })
    const API_BASE = 'http://localhost:8080/api'
    defineEmits(['navigate'])
    //Constantes
    const loading = ref(true)
    const background = ref(null)
    const expanded_feats = ref(new Set())

    //Metodos
    async function fetchBackgrounds() {
        loading.value = true
        try{        
            const res = await fetch(`${API_BASE}/backgrounds/${props.backgroundId}`, {
            headers: { Authorization: `Bearer ${props.token}` }
            })
            const data = await res.json()
            background.value = data

        }catch (e){
            console.error(e)
        }finally{
            loading.value = false
        }
    }

    onMounted(() => fetchBackgrounds())

    function toggleFeat(id) {
        if (expanded_feats.value.has(id)) {
            expanded_feats.value.delete(id)
        } else {
            expanded_feats.value.add(id)
        }
    }

    function renderDescription(text) {
        if (!text) return ''
        return marked(text)
    }

</script>

<template>
    <div class="background_page">
        <div v-if="loading" class="loading"> Cargando</div>
        <div v-else class="background_info">
            <h1 class="background_name">{{ background.name }}</h1>
            <p class="background_description">{{ background.description }}</p>

            <div v-if="background.skills?.length" class="background_skills">
                <span class="background_section_label">Skills:</span>
                <span v-for="s in background.skills" :key="s.skill" class="background_skill_chip">
                    {{ s.skill }} ({{ s.ability }})
                </span>
            </div>
            <div v-if="background.tools?.length" class="background_tools">
                    <span class="background_section_label">Tools:</span>
                    <span v-for="t in background.tools" :key="t.id" class="background_tool_chip" :title="`${t.weight} lb · ${t.rarity}`">
                        {{ t.name }}
                    </span>
                </div>

                <div v-if="background.feats?.length" class="background_feats_section">
                    <h2>Feats</h2>
                    <div v-for="f in background.feats" :key="f.id" class="background_feat_row">
                        <div class="background_feat_header" @click="toggleFeat(f.id)">
                            <span class="background_feat_name">
                                {{ f.name }}
                                <span v-if="f.repeatable" class="background_feat_repeatable">(repetible)</span>
                            </span>
                            <button class="background_feat_toggle_btn" :class="{ 'background_feat_toggle_btn--open': expanded_feats.has(f.id) }">
                                {{ expanded_feats.has(f.id) ? '−' : '+' }}
                            </button>
                        </div>
                        <div v-if="expanded_feats.has(f.id)" class="background_feat_description">
                            <p v-if="f.prerequisite" class="background_feat_prerequisite">
                                Prerrequisito: {{ f.prerequisite }}
                            </p>
                            <div v-html="renderDescription(f.description)"></div>
                        </div>
                    </div>
                </div>
        </div>
    </div>
    
</template>

<style>

</style>