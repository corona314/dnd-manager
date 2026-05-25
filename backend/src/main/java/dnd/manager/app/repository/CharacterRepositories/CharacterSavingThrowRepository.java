package dnd.manager.app.repository.CharacterRepositories;
 
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import dnd.manager.app.model.CharacterEntities.CharacterSavingThrow;
import dnd.manager.app.model.CharacterEntities.CharacterSavingThrowId;
 
public interface CharacterSavingThrowRepository extends JpaRepository<CharacterSavingThrow, CharacterSavingThrowId> {
    List<CharacterSavingThrow> findByCharacterId(Long characterId);
}
