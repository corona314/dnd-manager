package dnd.manager.app.repository.FeatRepositories.spec;

import org.springframework.data.jpa.domain.Specification;

import dnd.manager.app.model.Feat;

public class FeatSpecifications {

    public static Specification<Feat> hasName(String name) {
        return (root, query, cb) ->
            name == null || name.isBlank() ? null : cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<Feat> hasRepeatable(String repeatable) {
        return (root, query, cb) -> {
            if (repeatable == null || repeatable.isBlank()) {
                return null;
            }

            Boolean value;
            try {
                value = Boolean.parseBoolean(repeatable);
            } catch (Exception ex) {
                return null;
            }

            return cb.equal(root.get("repeatable"), value);
        };
    }
}
