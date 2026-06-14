package dnd.manager.app.model.ClassEntities;

import java.io.Serializable;
import java.util.Objects;

public class ClassSavingThrowId implements Serializable {
        private Long classEntity;
        private Long ability;

        public ClassSavingThrowId() {}

        public Long getClassEntity() { return classEntity; }
        public void setClassEntity(Long classEntity) { this.classEntity = classEntity; }

        public Long getAbility() { return ability; }
        public void setStat(Long ability) { this.ability = ability; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ClassSavingThrowId)) return false;
            ClassSavingThrowId that = (ClassSavingThrowId) o;
            return Objects.equals(classEntity, that.classEntity) && Objects.equals(ability, that.ability);
        }

        @Override
        public int hashCode() {
            return Objects.hash(classEntity, ability);
        }
    }
