package dnd.manager.app.repository.ItemRepositories.WeaponRepositories.spec;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import dnd.manager.app.model.ItemEntities.Item;
import dnd.manager.app.model.ItemEntities.WeaponEntities.Weapon;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;

public class WeaponSpecifications {


    public static Specification<Item> hasRangeBetween(Integer rangeMin, Integer rangeMax) {
        return (root, query, cb) -> {
            if (rangeMin == null && rangeMax == null) return null;
            Join<Item, Weapon> weapon = root.join("weapon");

            if (rangeMin != null && rangeMax != null) {
                return cb.and(
                    cb.lessThanOrEqualTo(weapon.get("rangeNormal"), rangeMax),
                    cb.greaterThanOrEqualTo(weapon.get("rangeLong"), rangeMin)
                );
            }

            if (rangeMin != null) {
                return cb.greaterThanOrEqualTo(weapon.get("rangeLong"), rangeMin);
            }

            return cb.lessThanOrEqualTo(weapon.get("rangeNormal"), rangeMax);
        };
    }

        public static Specification<Item> hasRangeNormalBetween(Integer rangeNormalMin, Integer rangeNormalMax) {
        return (root, query, cb) -> {
            if (rangeNormalMin == null && rangeNormalMax == null) return null;
            Join<Item, Weapon> weapon = root.join("weapon");

            
            if (rangeNormalMin != null && rangeNormalMax != null) {
                return cb.between(weapon.get("rangeNormal"), rangeNormalMin, rangeNormalMax);
            }

            if (rangeNormalMin != null) {
                return cb.greaterThanOrEqualTo(weapon.get("rangeNormal"), rangeNormalMin);
            }

            return cb.lessThanOrEqualTo(weapon.get("rangeNormal"), rangeNormalMax);
        };
    }

    public static Specification<Item> hasRangeLongBetween(Integer rangeLongMin, Integer rangeLongMax) {
        return (root, query, cb) -> {
            if (rangeLongMin == null && rangeLongMax == null) return null;
            
            Join<Item, Weapon> weapon = root.join("weapon");

            if (rangeLongMin != null && rangeLongMax != null) {
                return cb.between(weapon.get("rangeLong"), rangeLongMin, rangeLongMax);
            }

            if (rangeLongMin != null) {
                return cb.greaterThanOrEqualTo(weapon.get("rangeLong"), rangeLongMin);
            }

            return cb.lessThanOrEqualTo(weapon.get("rangeLong"), rangeLongMax);
        };
    }


    public static Specification<Item> hasMastery(String mastery) {
        return (root, query, cb) -> {
            if (mastery == null) return null;
            Join<Item, Weapon> weapon = root.join("weapon");

            return cb.equal(weapon.get("mastery").get("name"), mastery);
        };
    }

    public static Specification<Item> hasDamage(List<String> damageTypes) {
        return (root, query, cb) -> {
            if (damageTypes == null || damageTypes.isEmpty()) return null;

            Join<Item, Weapon> join = root.join("damageTypes", JoinType.INNER);
            return join.get("damageType").get("name").in(damageTypes);
        };
    }   
}
