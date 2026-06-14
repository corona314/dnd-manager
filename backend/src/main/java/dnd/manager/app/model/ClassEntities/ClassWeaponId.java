package dnd.manager.app.model.ClassEntities;

import java.io.Serializable;
import java.util.Objects;

import dnd.manager.app.model.ItemEntities.WeaponEntities.WeaponCategory;

public class ClassWeaponId implements Serializable {
        private Long classEntity;
        private WeaponCategory weaponCategory;

        public ClassWeaponId() {}

        public Long getClassEntity() { return classEntity; }
        public void setClassEntity(Long classId) { this.classEntity = classId; }

        public WeaponCategory getWeaponCategory() { return weaponCategory; }
        public void setWeaponCategory(WeaponCategory weaponCategoryId) { this.weaponCategory = weaponCategoryId; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ClassWeaponId)) return false;
            ClassWeaponId that = (ClassWeaponId) o;
            return Objects.equals(classEntity, that.classEntity) && Objects.equals(weaponCategory, that.weaponCategory);
        }

        @Override
        public int hashCode() {
            return Objects.hash(classEntity, weaponCategory);
        }
    }
