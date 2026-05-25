package dnd.manager.app.repository.TraitRepositories;
 
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import dnd.manager.app.model.TraitEntities.TraitType;
 
public interface TraitTypeRepository extends JpaRepository<TraitType, Long> {
    Optional<TraitType> findByName(String name);
}
