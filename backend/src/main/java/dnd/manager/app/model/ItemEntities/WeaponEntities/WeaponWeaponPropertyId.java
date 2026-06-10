package dnd.manager.app.model.ItemEntities.WeaponEntities;

import java.io.Serializable;
import java.util.Objects;

public class WeaponWeaponPropertyId implements Serializable {
        private Long weapon;
        private Long property;

        public WeaponWeaponPropertyId() {}

        public Long getWeapon() { return weapon; }
        public void setWeapon(Long character) { this.weapon = character; }

        public Long getProperty() { return property; }
        public void setProperty(Long skill) { this.property = skill; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof WeaponWeaponPropertyId)) return false;
            WeaponWeaponPropertyId that = (WeaponWeaponPropertyId) o;
            return Objects.equals(weapon, that.weapon) && Objects.equals(property, that.property);
        }

        @Override
        public int hashCode() {
            return Objects.hash(weapon, property);
        }
    }