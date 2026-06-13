package dnd.manager.app.model.ClassEntities;

import java.io.Serializable;
import java.util.Objects;

public class ClassArmorTypeId implements Serializable {
        private Long classId;
        private Long armorTypeId;

        public ClassArmorTypeId() {}

        public Long getClassId() { return classId; }
        public void setClassId(Long classId) { this.classId = classId; }

        public Long getArmorTypeId() { return armorTypeId; }
        public void setArmorTypeId(Long armorTypeId) { this.armorTypeId = armorTypeId; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ClassArmorTypeId)) return false;
            ClassArmorTypeId that = (ClassArmorTypeId) o;
            return Objects.equals(classId, that.classId) && Objects.equals(armorTypeId, that.armorTypeId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(classId, armorTypeId);
        }
    }
