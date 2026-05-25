package dnd.manager.app.model.ClassEntities;

import java.io.Serializable;
import java.util.Objects;

public class ClassSavingThrowId implements Serializable {
        private Long classEntity;
        private Long stat;

        public ClassSavingThrowId() {}

        public Long getClassEntity() { return classEntity; }
        public void setClassEntity(Long classEntity) { this.classEntity = classEntity; }

        public Long getStat() { return stat; }
        public void setStat(Long stat) { this.stat = stat; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ClassSavingThrowId)) return false;
            ClassSavingThrowId that = (ClassSavingThrowId) o;
            return Objects.equals(classEntity, that.classEntity) && Objects.equals(stat, that.stat);
        }

        @Override
        public int hashCode() {
            return Objects.hash(classEntity, stat);
        }
    }
