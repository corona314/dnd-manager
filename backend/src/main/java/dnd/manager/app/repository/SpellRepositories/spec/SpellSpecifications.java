package dnd.manager.app.repository.SpellRepositories.spec;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import dnd.manager.app.model.SpellEntities.Spell;
import jakarta.persistence.criteria.Predicate;

public class SpellSpecifications {

    public static Specification<Spell> hasName(String name){
        return (root, query, cb) ->
            name == null ? null : cb.like(cb.lower(root.get("name")), "%"+name.toLowerCase()+"%");
    }

    public static Specification<Spell> hasLevel(Integer level){
        return (root, query, cb) ->
            level == null ? null : cb.equal(root.get("level"), level);
    }

    public static Specification<Spell> hasSchool(Integer schoolId){
        return (root, query, cb) ->
            schoolId == null ? null : cb.equal(root.get("school").get("id"), schoolId);
    }

    public static Specification<Spell> hasComponent(String components){
        return (root, query, cb) -> {
            
            if(components == null) return null;
            
            List<Predicate> predicates = new ArrayList<>();
    
            if (components.contains("S")){
                if (components.contains("!S")) predicates.add(cb.notLike(root.get("components"), "%S%"));
                else predicates.add(cb.like(root.get("components"), "%S%"));
            }
            if (components.contains("M")){
                if (components.contains("!M")) predicates.add(cb.notLike(root.get("components"), "%M%"));
                else predicates.add(cb.like(root.get("components"), "%M%"));
                
            }
            if (components.contains("V")){
                if (components.contains("!V")) predicates.add(cb.notLike(root.get("components"), "%V%"));
                else predicates.add(cb.like(root.get("components"), "%V%"));
            }
            return predicates.isEmpty() ? null : cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Spell> isConcentration(Boolean concentration){
        return (root, query, cb) ->
            concentration == null ? null : cb.equal(root.get("concentration"), concentration);
    }

    public static Specification<Spell> isRitual(Boolean ritual){
        return (root, query, cb) ->
            ritual == null ? null : cb.equal(root.get("ritual"), ritual);
    }

    public static Specification<Spell> hasSavingThrowStat(String stat){
        return (root, query, cb) ->
            stat == null ? null : cb.equal(root.get("savingThrowStat").get("code"), stat);
    }

    public static Specification<Spell> isAttackRoll(Boolean attackRoll){
        return (root, query, cb) ->
            attackRoll == null ? null : cb.equal(root.get("attackRoll"), attackRoll);
    }    
    
    public static Specification<Spell> hasDamage(String damageType){
        return (root, query, cb) ->
            damageType == null ? null : cb.equal(root.get("damageType").get("name"), damageType);
    }

}
