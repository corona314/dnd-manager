<script setup>
    import './styles/appClassExpansion.css'
    import { marked } from 'marked'
    import { ref, onMounted} from 'vue'
    const props = defineProps({ token: String, classId: Number })
    const API_BASE = 'http://localhost:8080/api'
    defineEmits(['navigate'])
    //Constantes
    const loading = ref(true)
    const class_data = ref(null)
    const expanded_features = ref(new Set())

    //Metodos
    async function fetchClasses() {
        loading.value = true
        try{        
            const res = await fetch(`${API_BASE}/classes/${props.classId}`, {
            headers: { Authorization: `Bearer ${props.token}` }
            })
            const data = await res.json()
            class_data.value = data

        }catch (e){
            console.error(e)
        }finally{
            loading.value = false
        }
    }

    onMounted(() => fetchClasses())

    function toggleFeature(id) {
        if (expanded_features.value.has(id)) {
            expanded_features.value.delete(id)
        } else {
            expanded_features.value.add(id)
        }
    }

    function featuresByLevel() {
        if (!class_data.value?.features) return []
        const levels = [...new Set(class_data.value.features.map(f => f.level))].sort((a, b) => a - b)
        return levels.map(level => ({
            level,
            features: class_data.value.features.filter(f => f.level === level)
        }))
    }

    function renderDescription(text) {
        if (!text) return ''
        return marked(text)
    }
</script>

<template>
    <div class="class_page">
        <div v-if="loading" class="loading"> Cargando</div>
        <div v-else class="class_info">
            <h1 class="class_name">{{ class_data.name }}</h1>
            <div class="class_hitdie">
                <span class="class_hitdie_label">Hit Point Die: </span>
                <span class="class_hitdie_chip">{{ class_data.hitPointDie }}</span>
            </div>
            

            <div v-if="class_data.savingThrows?.length" class="class_saving_throws">
                <span class="class_section_label">Saving Throw Proficiencies:</span>
                <span v-for="s in class_data.savingThrows" :key="s.ability" class="class_saving_throw_chip">
                    {{ s.ability }}
                </span>
            </div>

            <div v-if="class_data.skills?.length" class="class_skills">
                <span class="class_section_label">Skill Proficiencies (Choose {{ class_data.numberSkills }}):</span>
                <span v-for="s in class_data.skills" :key="s.skill" class="class_skill_chip">
                    {{ s.skill }} ({{ s.ability }})
                </span>
            </div>

            <div v-if="class_data.armorTypes?.length" class="class_armor_types">
                <span class="class_section_label">Armor Training</span>
                <span v-for="a in class_data.armorTypes" :key="a.armorType" class="class_armor_chip">
                    {{ a.armorType}}
                </span> 
            </div>
            <div v-else class="class_armor_types">
                <span class="class_section_label">Armor Training</span>
                <span class="class_armor_chip">
                    None
                </span> 
            </div>

            <div v-if="class_data.features?.length" class="class_features_section">
                <h2>Feats by level:</h2>
                <div v-for="group in featuresByLevel()" :key="group.level" class="class_feature_level_group">
                    <h3 class="class_feature_level_title">Nivel {{ group.level }}</h3>
                    <div v-for="f in group.features" :key="f.feature.id" class="class_feature_row">
                        <div class="class_feature_header" @click="toggleFeature(f.feature.id)">
                            <span class="class_feature_name">{{ f.feature.name }}</span>
                            <button class="class_feature_toggle_btn" :class="{ 'class_feature_toggle_btn--open': expanded_features.has(f.feature.id) }">
                                {{ expanded_features.has(f.feature.id) ? '−' : '+' }}
                            </button>
                        </div>
                        <div v-if="expanded_features.has(f.feature.id)" class="class_feature_description" v-html="renderDescription(f.feature.description)"></div>
                    </div>
                </div>
            </div>

            <div v-if="class_data.subclasses?.length" class="class_subclasses">
                <span class="class_section_label">Subclases:</span>
                <button v-for="p in class_data.subclasses" :key="p.id" class="class_subclasses_chip"
                    @click="$emit('navigate', { page: 'subclassExtended', subclassId: p.id })">
                    {{ p.name }}
                </button>
            </div>
        </div>
    </div>
    
</template>
