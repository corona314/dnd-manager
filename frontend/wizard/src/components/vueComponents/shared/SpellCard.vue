<script setup>
    import { marked } from 'marked'
    import AttackIcon from '@/icons/AttackIcon.vue'
    import DefenseIcon from '@/icons/DefenseIcon.vue'
    import CastingTimeIcon from '@/icons/CastingTimeIcon.vue'
    import RangeIcon from '@/icons/RangeIcon.vue'
    import DurationIcon from '@/icons/DurationIcon.vue'
    import MaterialIcon from '@/icons/MaterialIcon.vue'
    import NoMaterialIcon from '@/icons/NoMaterialIcon.vue'
    import RitualIcon from '@/icons/RitualIcon.vue'
    import NoRitualIcon from '@/icons/NoRitualIcon.vue'
    import ConcentrationIcon from '@/icons/ConcentrationIcon.vue'
    import NoConcentrationIcon from '@/icons/NoConcentrationIcon.vue'
    import VocalIcon from '@/icons/VocalIcon.vue'
    import NoVocalIcon from '@/icons/NoVocalIcon.vue'
    import SomaticIcon from '@/icons/SomaticIcon.vue'
    import NoSomaticIcon from '@/icons/NoSomaticIcon.vue'
    import { usingIcons } from '@/composables/usePreferences.js'

    const props = defineProps({
        spell: { type: Object, required: true },
        expanded: Boolean,
        detail: Object,            // conjuro completo (cuando está expandido)
        loading: Boolean,          // cargando el detalle
        upcastLevel: Number,       // nivel de upcast seleccionado
        selectable: Boolean,       // muestra el checkbox (personaje)
        selected: Boolean,
        selectDisabled: Boolean
    })

    const emit = defineEmits(['expand', 'toggle-select', 'select-upcast'])

    const Damage_Colors = {Acid: '--damage-acid', Cold: '--damage-cold', Fire: '--damage-fire', Force: '--damage-force', Lightning: '--damage-lightning', Necrotic: '--damage-necrotic', Piercing: '--damage-piercing', Poison: '--damage-poison', Psychic: '--damage-psychic', Radiant: '--damage-radiant', Slashing: '--damage-slashing', Thunder: '--damage-thunder', Bludgeoning: '--damage-bludgeoning'}

    const Damage_Text = {
        Fire: '--text-on-light',
        Cold: '--text-on-light',
        Lightning: '--text-on-light',
        Poison: '--text-on-light',
        Acid: '--text-on-light',
        Necrotic: '--text-on-dark',
        Radiant: '--text-on-light',
        Psychic: '--text-on-dark',
        Force: '--text-on-dark',
        Thunder: '--text-on-dark',
        Piercing: '--text-on-dark',
        Slashing: '--text-on-dark',
        Bludgeoning: '--text-on-dark'
    }

    function damageTypes(spell) {
        return (spell.damageTypes ?? []).map(d => d.damageType).filter(Boolean)
    }

    function getBackground(spell) {
        const types = damageTypes(spell)
        if (types.length === 0) return 'var(--surface)'

        const colors = types.map(t => `var(${Damage_Colors[t] || '--surface'})`)
        if (colors.length === 1) return colors[0]

        const stops = colors.map((color, i) => {
            const position = (i / (colors.length - 1)) * 100
            return `${color} ${position}%`
        }).join(', ')

        return `linear-gradient(45deg, ${stops})`
    }

    function getTextColor(spell) {
        const types = damageTypes(spell)
        if (types.length === 0) return 'var(--text)'

        let darkVotes = 0
        let lightVotes = 0
        for (const t of types) {
            if (Damage_Text[t] === '--text-on-dark') darkVotes++
            else lightVotes++
        }
        return darkVotes > lightVotes ? 'var(--text-on-dark)' : 'var(--text-on-light)'
    }

    function getUpcastText(spell, level) {
        const type = spell.damageTypes?.[0]?.damageType
        if (level === spell.level) {
            return spell.damageRoll ? `${spell.damageRoll} ${type ?? ''}` : null
        }
        const upcast = spell.upcasts.find(u => u.level === level)
        if (!upcast) return null
        if (upcast.description) return upcast.description
        if (upcast.damageRoll) return type ? `${upcast.damageRoll} ${type} damage` : upcast.damageRoll
        return null
    }

    function renderDescription(text) {
        if (!text) return ''
        const fixed = text
            .replace(/\|\s*\|/g, '|\n|')
            .replace(/(Table:[^\n|]+)\|/, '$1\n|')
        return marked(fixed)
    }
</script>

<template>
    <div
        class="spell_card"
        :style="{ background: getBackground(spell), color: getTextColor(spell) }"
        :class="{ 'spell_card--expanded': expanded, 'spell_card--selected': selected }"
        v-no-double-select
        @click="emit('expand', spell)"
    >
        <div class="spell_card_header">
            <label v-if="selectable" class="spell_select" @click.stop>
                <input
                    type="checkbox"
                    :checked="selected"
                    :disabled="selectDisabled"
                    :aria-label="`Select ${spell.name}`"
                    @change="emit('toggle-select', spell)"
                />
            </label>

            <div class="spell_card_base">
                <span class="spell_name">{{ spell.name }}</span>
                <span v-if="spell.attackRoll" class="spell_attackroll">    
                    <AttackIcon v-if="usingIcons" /><template v-else>A</template>
                </span>
                <span v-if="spell.savingThrowAbility" class="spell_savingthrow">
                    <DefenseIcon v-if="usingIcons" /><template v-else>ST</template>
                </span>

                <span class="spell_component_v" title="Verbal" :class="{ icon_off: !spell.components.includes('V') }">
                    <template v-if="usingIcons">
                        <VocalIcon v-if="spell.components.includes('V')" />
                        <NoVocalIcon v-else />
                    </template>
                    <template v-else>V</template>
                </span>

                <span class="spell_component_s" title="Somatic" :class="{ icon_off: !spell.components.includes('S') }">
                    <template v-if="usingIcons">
                        <SomaticIcon v-if="spell.components.includes('S')" />
                        <NoSomaticIcon v-else />
                    </template>
                    <template v-else>S</template>
                </span>

                <span class="spell_component_m" title="Material" :class="{ icon_off: !spell.components.includes('M') }">
                    <template v-if="usingIcons">
                        <MaterialIcon v-if="spell.components.includes('M')" />
                        <NoMaterialIcon v-else />
                    </template>
                    <template v-else>M</template>
                </span>
                
                <span class="spell_level">{{ spell.level === 0 ? 'Cantrip' : `Lvl. ${spell.level}` }}</span>
                <span class="spell_school">{{ spell.school }}</span>

                <span class="spell_ritual" :class="{ 'spell_ritual--active': spell.ritual, 'spell_ritual--text': !usingIcons }">
                    <template v-if="usingIcons">
                        <RitualIcon v-if="spell.ritual" />
                        <NoRitualIcon v-else />
                    </template>
                    <template v-else>R</template>
                </span>

                <span class="spell_concentration" :class="{ 'spell_concentration--active': spell.concentration, 'spell_concentration--text': !usingIcons }">                    
                <template v-if="usingIcons">
                        <ConcentrationIcon v-if="spell.concentration" />
                        <NoConcentrationIcon v-else />
                    </template>
                    <template v-else>C</template>
                </span>
            </div>
        </div>

        <div v-if="expanded" class="spell_card_expanded" @click.stop>
            <div v-if="loading">Loading...</div>
            <div v-else-if="detail" class="spell_card_expanded_content">
                <div class="spell_details">
                    <span><CastingTimeIcon class="spell_detail_icon" /> {{ detail.castingTime }}</span>
                    <span><RangeIcon class="spell_detail_icon" /> {{ detail.range }}</span>
                    <span><DurationIcon class="spell_detail_icon" /> {{ detail.duration }}</span>
                    <span v-if="detail.material">
                        <MaterialIcon class="spell_detail_icon" /> {{ detail.material }}
                    </span>
                </div>

                <p class="spell_desc" v-html="renderDescription(detail.description)"></p>

                <div v-if="detail.upcasts.length && detail.upcasts[0].upcastType === 'SLOT'" class="upcast_section">
                    <div class="upcast_levels">
                        <span
                            v-for="lvl in detail.upcasts.map(u => u.level)"
                            :key="lvl"
                            class="upcast_button"
                            :class="{ 'upcast_button--active': upcastLevel === lvl }"
                            @click="emit('select-upcast', lvl)"
                        >{{ lvl }}</span>
                    </div>
                    <div class="upcast_result">
                        {{ getUpcastText(detail, upcastLevel) }}
                    </div>
                </div>

                <div v-else-if="detail.upcasts.length && detail.upcasts[0].upcastType === 'CANTRIP'" class="upcast_section">
                    <div class="upcast_cantrip_row">
                        <span class="upcast_cantrip_step">
                            Char. Lvl 1: {{ detail.damageRoll }}
                        </span>
                        <span v-for="u in detail.upcasts" :key="u.level" class="upcast_cantrip_step">
                            - -> Char. Lvl {{ u.level }}: {{ u.damageRoll ?? u.description }}
                        </span>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped>
    /* Cabecera: fila con el checkbox (si hay) y la base de la tarjeta.
       Mismo alto que .spell_card_base (40px), así la tarjeta sigue midiendo 60px. */
    .spell_card_header {
        display: flex;
        flex-direction: row;
        align-items: flex-start;
        gap: 10px;
        width: 100%;
        height: 40px;
        flex-shrink: 0;
    }

    .spell_card_header > .spell_card_base {
        width: auto;
        flex: 1;
    }

    .spell_select {
        flex-shrink: 0;
        padding-top: 2px;
        cursor: pointer;
    }

    .spell_card--selected {
        border-color: var(--accent);
    }
</style>