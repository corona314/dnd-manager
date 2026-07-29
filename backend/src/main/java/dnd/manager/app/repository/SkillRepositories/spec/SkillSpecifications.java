package dnd.manager.app.repository.SkillRepositories.spec;

import org.springframework.data.jpa.domain.Specification;

import dnd.manager.app.model.Skill;

public class SkillSpecifications {

    public static Specification<Skill> hasName(String name){
        return (root, query, cb) ->
            name == null ? null : cb.like(cb.lower(root.get("name")), "%"+name.toLowerCase()+"%");
    }

    public static Specification<Skill> hasAbility(String ability){
        return (root, query, cb) ->
            ability == null ? null : cb.like(root.get("ability").get("code"), ability);
    }
}