package dnd.manager.app.repository.SpellRepositories.spec;

import org.springframework.data.jpa.domain.Specification;

import dnd.manager.app.model.SpellEntities.Spell;

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

    public static Specification<Spell> hasVerbalComponent(String components){
        return (root, query, cb) ->
            components == null ? null : cb.like(root.get("components"), "%V%");
    }

    
    public static Specification<Spell> hasSomaticComponent(String components){
        return (root, query, cb) ->
        components == null ? null : cb.like(root.get("components"), "%S%");
    }
    
    public static Specification<Spell> hasMaterialComponent(String components){
        return (root, query, cb) ->
            components == null ? null : cb.like(root.get("components"), "%M%");
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
