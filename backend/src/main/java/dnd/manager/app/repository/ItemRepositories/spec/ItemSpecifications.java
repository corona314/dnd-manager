package dnd.manager.app.repository.ItemRepositories.spec;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import dnd.manager.app.model.ItemEntities.Item;

public class ItemSpecifications {

    public static Specification<Item> hasName(String name){
        return (root, query, cb) ->
            name == null ? null : cb.like(cb.lower(root.get("name")), "%"+name.toLowerCase()+"%");
    }

    public static Specification<Item> hasWeightBetween(Float weightMin, Float weightMax) {
        return (root, query, cb) -> {
            if (weightMin == null && weightMax == null) return null;
            
            if (weightMin != null && weightMax != null) {
                return cb.between(root.get("weight"), weightMin, weightMax);
            }

            if (weightMin != null) {
                return cb.greaterThanOrEqualTo(root.get("weight"), weightMin);
            }

            return cb.lessThanOrEqualTo(root.get("weight"), weightMax);
        };
    }

    public static Specification<Item> hasPriceBetween(Integer priceMin, Integer priceMax) {
        return (root, query, cb) -> {
            if (priceMin == null && priceMax == null) return null;
            
            if (priceMin != null && priceMax != null) {
                return cb.between(root.get("weight"), priceMin, priceMax);
            }

            if (priceMin != null) {
                return cb.greaterThanOrEqualTo(root.get("weight"), priceMin);
            }

            return cb.lessThanOrEqualTo(root.get("weight"), priceMax);
        };
    }

    public static Specification<Item> hasItemType(List<String> itemType) {
        return (root, query, cb) -> 
            itemType == null || itemType.isEmpty() ? null : cb.in(root.get("itemType").get("name")).value(itemType);
    }

    public static Specification<Item> isMagic(Boolean magic) {
        return (root, query, cb) -> 
            magic == null ? null : cb.equal(root.get("magic"), magic);  
    }

    public static Specification<Item> hasAttunement(Boolean attunement) {
        return (root, query, cb) ->
            attunement == null ? null : cb.equal(root.get("attunement"), attunement);
    }

    public static Specification<Item> hasRarity(List<String> rarity) {
        return (root, query, cb) -> 
            rarity == null || rarity.isEmpty() ? null : cb.in(root.get("rarity")).value(rarity);
    }
}