package dnd.manager.app.repository.ClassRepositories.spec;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import dnd.manager.app.model.ClassEntities.ClassEntity;

public class ClassSpecifications {

    public static Specification<ClassEntity> hasName(String name){
        return (root, query, cb) ->
            name == null ? null : cb.like(cb.lower(root.get("name")), "%"+name.toLowerCase()+"%");
    }

    public static Specification<ClassEntity> hasHitPointDie(List<String> hitPointDie) {
        return (root, query, cb) -> 
            hitPointDie == null || hitPointDie.isEmpty() ? null : cb.in(root.get("hitPointDie").get("name")).value(hitPointDie);
    }

}