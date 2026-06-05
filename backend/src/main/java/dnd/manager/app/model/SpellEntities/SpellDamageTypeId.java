package dnd.manager.app.model.SpellEntities;

import java.io.Serializable;
import java.util.Objects;

public class SpellDamageTypeId implements Serializable {
        private Long spell;
        private Long damageType;

        public SpellDamageTypeId() {}

        public Long getSpell() { return spell; }
        public void setSpell(Long character) { this.spell = character; }

        public Long getDamageType() { return damageType; }
        public void setDamageType(Long skill) { this.damageType = skill; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof SpellDamageTypeId)) return false;
            SpellDamageTypeId that = (SpellDamageTypeId) o;
            return Objects.equals(spell, that.spell) && Objects.equals(damageType, that.damageType);
        }

        @Override
        public int hashCode() {
            return Objects.hash(spell, damageType);
        }
    }