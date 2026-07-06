package dnd.manager.app.repository.SpeciesRepositories.spec;

import org.springframework.data.jpa.domain.Specification;

import dnd.manager.app.model.SpeciesEntities.Species;


public class SpeciesSpecifications {

    public static Specification<Species> hasName(String name){
        return (root, query, cb) ->
            name == null ? null : cb.like(cb.lower(root.get("name")), "%"+name.toLowerCase()+"%");
    }

    public static Specification<Species> hasSize(String size){
        return (root, query, cb) ->
            size == null ? null : cb.equal(root.get("size"), size);
    }

    public static Specification<Species> hasWalkSpeed(Integer walkSpeed){
        return (root, query, cb) ->
            walkSpeed == null ? null : cb.equal(root.get("walkSpeed"), walkSpeed);
    }
} 