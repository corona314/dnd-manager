package dnd.manager.app.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import dnd.manager.app.model.Trait;

public interface TraitRepository extends JpaRepository<Trait, Long> {

    List<Trait> findByTraitTypeId(Long traitTypeId);

}
