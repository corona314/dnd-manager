package dnd.manager.app.repository;
 
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import dnd.manager.app.model.TraitType;
 
public interface TraitTypeRepository extends JpaRepository<TraitType, Long> {
    Optional<TraitType> findByName(String name);
}
