package dnd.manager.app.repository.ItemRepositories.spec;

import org.springframework.data.jpa.domain.Specification;
import dnd.manager.app.model.ItemEntities.Item;
import dnd.manager.app.model.ItemEntities.ShieldEntities.Shield;
import jakarta.persistence.criteria.Join;

public class ShieldSpecifications {


    public static Specification<Item> hasAcBonus(Integer acBonus) {
        return (root, query, cb) -> {
            if (acBonus == null) return null;
            Join<Item, Shield> shield = root.join("shield");
            return cb.equal(shield.get("acBonus").get("name"), acBonus);
        };
    }
}
