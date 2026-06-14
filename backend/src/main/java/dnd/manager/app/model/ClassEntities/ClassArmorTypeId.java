package dnd.manager.app.model.ClassEntities;

import java.io.Serializable;
import java.util.Objects;

public class ClassArmorTypeId implements Serializable {
        private Long classEntity;
        private Long armorType;

        public ClassArmorTypeId() {}

        public Long getClassEntity() { return classEntity; }
        public void setClassEntity(Long classId) { this.classEntity = classId; }

        public Long getArmorType() { return armorType; }
        public void setArmorType(Long armorTypeId) { this.armorType = armorTypeId; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ClassArmorTypeId)) return false;
            ClassArmorTypeId that = (ClassArmorTypeId) o;
            return Objects.equals(classEntity, that.classEntity) && Objects.equals(armorType, that.armorType);
        }

        @Override
        public int hashCode() {
            return Objects.hash(classEntity, armorType);
        }
    }
