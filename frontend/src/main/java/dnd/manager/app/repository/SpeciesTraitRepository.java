package dnd.manager.app.repository;
 
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import dnd.manager.app.model.SpeciesTrait;
import dnd.manager.app.model.SpeciesTraitId;
 
public interface SpeciesTraitRepository extends JpaRepository<SpeciesTrait, SpeciesTraitId> {
    List<SpeciesTrait> findBySpeciesId(Long speciesId);
}
