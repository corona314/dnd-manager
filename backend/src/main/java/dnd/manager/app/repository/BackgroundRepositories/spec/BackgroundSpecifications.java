package dnd.manager.app.repository.BackgroundRepositories.spec;

import org.springframework.data.jpa.domain.Specification;

import dnd.manager.app.model.BackgroundEntities.Background;

public class BackgroundSpecifications {

        public static Specification<Background> hasName(String name){
        return (root, query, cb) ->
            name == null ? null : cb.like(cb.lower(root.get("name")), "%"+name.toLowerCase()+"%");
        }
}
