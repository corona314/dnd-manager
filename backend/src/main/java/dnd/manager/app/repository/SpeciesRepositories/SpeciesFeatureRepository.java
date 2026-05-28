package dnd.manager.app.repository.SpeciesRepositories;
 
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import dnd.manager.app.model.SpeciesEntities.SpeciesFeature;
import dnd.manager.app.model.SpeciesEntities.SpeciesFeatureId;
 
public interface SpeciesFeatureRepository extends JpaRepository<SpeciesFeature, SpeciesFeatureId> {
    List<SpeciesFeature> findBySpeciesId(Long speciesId);
}
