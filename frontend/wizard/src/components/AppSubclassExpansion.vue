<script setup>
    import './styles/appSubclassExpansion.css'
    import { marked } from 'marked'
    import { ref, onMounted} from 'vue'
    const props = defineProps({ token: String, subclassId: Number })
    const API_BASE = 'http://localhost:8080/api'
    //Constantes
    const loading = ref(true)
    const subclass= ref(null)
    const expanded_features = ref(new Set())

    //Metodos
    async function fetchClasses() {
        loading.value = true
        try{        
            const res = await fetch(`${API_BASE}/classes/subclasses/${props.subclassId}`, {
            headers: { Authorization: `Bearer ${props.token}` }
            })
            const data = await res.json()
            subclass.value = data

        }catch (e){
            console.error(e)
        }finally{
            loading.value = false
        }
    }

    onMounted(() => fetchClasses())

    function toggleFeature(name) {
        if (expanded_features.value.has(name)) {
            expanded_features.value.delete(name)
        } else {
            expanded_features.value.add(name)
        }
    }

    function featuresByLevel() {
        if (!subclass.value?.features) return []
        const levels = [...new Set(subclass.value.features.map(f => f.level))].sort((a, b) => a - b)
        return levels.map(level => ({
            level,
            features: subclass.value.features.filter(f => f.level === level)
        }))
    }

    function renderDescription(text) {
        if (!text) return ''
        return marked(text)
    }
</script>

<template>
    <div class="subclass_page">
        <div v-if="loading" class="loading"> Cargando</div>
        <div v-else class="subclass_info">
            <h1 class="subclass_name">{{ subclass.name }}</h1>
            <div v-if="subclass.features?.length" class="subclass_features">
                <h2>Feats by level:</h2>
                <div v-for="group in featuresByLevel()" :key="group.level" class="subclass_feature_level_group">
                    <h3 class="subclass_feature_level_title">Level {{ group.level }}</h3>
                    <div v-for="f in group.features" :key="f.feature.name" class="subclass_feature_row">
                        <div class="subclass_feature_header" @click="toggleFeature(f.feature.name)">
                            <span class="subclass_feature_name">{{ f.feature.name }}</span>
                            <button class="subclass_feature_toggle_btn" :class="{ 'subclass_feature_toggle_btn--open': expanded_features.has(f.feature.name) }">
                                {{ expanded_features.has(f.feature.name) ? '−' : '+' }}
                            </button>
                        </div>
                        <div v-if="expanded_features.has(f.feature.name)" class="subclass_feature_description" v-html="renderDescription(f.feature.description)"></div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
</template>
