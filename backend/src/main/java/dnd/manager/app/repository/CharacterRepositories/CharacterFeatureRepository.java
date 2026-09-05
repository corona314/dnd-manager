package dnd.manager.app.repository.CharacterRepositories;
 
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import dnd.manager.app.model.CharacterEntities.CharacterFeature;
import dnd.manager.app.model.CharacterEntities.CharacterFeatureId;
 
public interface CharacterFeatureRepository extends JpaRepository<CharacterFeature, CharacterFeatureId> {
    List<CharacterFeature> findByCharacterId(Long characterId);
}
