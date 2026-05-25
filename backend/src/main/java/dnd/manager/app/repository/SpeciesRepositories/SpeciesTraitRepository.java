package dnd.manager.app.repository.SpeciesRepositories;
 
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import dnd.manager.app.model.SpeciesEntities.SpeciesTrait;
import dnd.manager.app.model.SpeciesEntities.SpeciesTraitId;
 
public interface SpeciesTraitRepository extends JpaRepository<SpeciesTrait, SpeciesTraitId> {
    List<SpeciesTrait> findBySpeciesId(Long speciesId);
}
