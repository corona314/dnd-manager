package dnd.manager.app.repository.ItemRepositories.ArmorRepositories.spec;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import dnd.manager.app.model.ItemEntities.Item;

import dnd.manager.app.model.ItemEntities.ArmorEntities.Armor;
import dnd.manager.app.model.ItemEntities.ArmorEntities.ArmorType;
import jakarta.persistence.criteria.Join;

public class ArmorSpecifications {

    //Parto desde Specification<Item> y hago join a Armor para poder juntar los dos specs en el mismo método (tanto el de item como el de armor)

    public static Specification<Item> hasAcBetween(Integer acMin, Integer acMax) {
        return (root, query, cb) -> {
            if (acMin == null && acMax == null) return null;
            Join<Item, Armor> armor = root.join("armor");

            if (acMin != null && acMax != null) {
                return cb.and(
                    cb.lessThanOrEqualTo(armor.get("acBase"), acMax),
                    cb.greaterThanOrEqualTo(armor.get("acMax"), acMin)
                );
            }

            if (acMin != null) {
                return cb.greaterThanOrEqualTo(armor.get("acMax"), acMin);
            }

            return cb.lessThanOrEqualTo(armor.get("acBase"), acMax);
        };
    }

    public static Specification<Item> hasStrMinLessThanOrEqualTo(Integer strMin){
        return (root, query, cb) -> {
            if (strMin == null) return null;
            Join<Item, Armor> armor = root.join("armor");
            return cb.lessThanOrEqualTo(armor.get("strMin"), strMin);
        };
    }

    public static Specification<Item> hasStealthDis(Boolean stealthDis){
        return (root, query, cb) -> {
            if (stealthDis == null) return null;
            Join<Item, Armor> armor = root.join("armor");
            return cb.equal(armor.get("stealthDis"), stealthDis);
        };
    }

    public static Specification<Item> hasArmorType(List<String> armorType) {
        return (root, query, cb) -> {
            if (armorType == null || armorType.isEmpty()) return null;
            Join<Item, Armor> armor = root.join("armor");
            Join<Armor, ArmorType> armorTypeJoin = armor.join("armorType");
            return cb.in(armorTypeJoin.get("name")).value(armorType);
        };
    }
}
