package dnd.manager.app.repository.TraitRepositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import dnd.manager.app.model.TraitEntities.Trait;

public interface TraitRepository extends JpaRepository<Trait, Long> {

    List<Trait> findByTraitTypeId(Long traitTypeId);

}
