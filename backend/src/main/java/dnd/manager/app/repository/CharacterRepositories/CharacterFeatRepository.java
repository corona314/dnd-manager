package dnd.manager.app.repository.CharacterRepositories;
 
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import dnd.manager.app.model.CharacterEntities.CharacterFeat;
 
public interface CharacterFeatRepository extends JpaRepository<CharacterFeat, Long> {
    List<CharacterFeat> findByCharacterId(Long characterId);
    List<CharacterFeat> findByCharacterIdAndSource(Long characterId, String source);
}
