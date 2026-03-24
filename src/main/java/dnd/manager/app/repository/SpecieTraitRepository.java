package dnd.manager.app.repository;
 
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import dnd.manager.app.model.SpecieTrait;
import dnd.manager.app.model.SpecieTraitId;
 
public interface SpecieTraitRepository extends JpaRepository<SpecieTrait, SpecieTraitId> {
    List<SpecieTrait> findBySpecieId(Long specieId);
}
