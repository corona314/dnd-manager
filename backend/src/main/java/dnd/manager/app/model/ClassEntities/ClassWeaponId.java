package dnd.manager.app.model.ClassEntities;

import java.io.Serializable;
import java.util.Objects;

public class ClassWeaponId implements Serializable {
        private Long classId;
        private Long weaponCategoryId;

        public ClassWeaponId() {}

        public Long getClassId() { return classId; }
        public void setClassId(Long classId) { this.classId = classId; }

        public Long getWeaponCategoryId() { return weaponCategoryId; }
        public void setWeaponCategoryId(Long weaponCategoryId) { this.weaponCategoryId = weaponCategoryId; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ClassWeaponId)) return false;
            ClassWeaponId that = (ClassWeaponId) o;
            return Objects.equals(classId, that.classId) && Objects.equals(weaponCategoryId, that.weaponCategoryId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(classId, weaponCategoryId);
        }
    }
