package dnd.manager.app.model.ItemEntities.WeaponEntities;

import java.io.Serializable;
import java.util.Objects;

public class WeaponDamageId implements Serializable {
        private Long weapon;
        private Long damageType;

        public WeaponDamageId() {}

        public Long getWeapon() { return weapon; }
        public void setWeapon(Long character) { this.weapon = character; }

        public Long getDamageType() { return damageType; }
        public void setDamageType(Long skill) { this.damageType = skill; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof WeaponDamageId)) return false;
            WeaponDamageId that = (WeaponDamageId) o;
            return Objects.equals(weapon, that.weapon) && Objects.equals(damageType, that.damageType);
        }

        @Override
        public int hashCode() {
            return Objects.hash(weapon, damageType);
        }
    }