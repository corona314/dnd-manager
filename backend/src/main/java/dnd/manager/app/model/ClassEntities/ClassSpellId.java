package dnd.manager.app.model.ClassEntities;

import java.io.Serializable;
import java.util.Objects;

public class ClassSpellId implements Serializable {
        private Long classEntity;
        private Long spell;

        public ClassSpellId() {}

        public Long getClassEntity() { return classEntity; }
        public void setClassEntity(Long classId) { this.classEntity = classId; }

        public Long getSpell() { return spell; }
        public void setSpell(Long spellId) { this.spell = spellId; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ClassSpellId)) return false;
            ClassSpellId that = (ClassSpellId) o;
            return Objects.equals(classEntity, that.classEntity) && Objects.equals(spell, that.spell);
        }

        @Override
        public int hashCode() {
            return Objects.hash(classEntity, spell);
        }
    }
