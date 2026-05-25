package dnd.manager.app.repository.CharacterRepositories;
 
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import dnd.manager.app.model.CharacterEntities.CharacterSpell;
 
public interface CharacterSpellRepository extends JpaRepository<CharacterSpell, Long> {
    List<CharacterSpell> findByCharacterId(Long characterId);
    List<CharacterSpell> findByCharacterIdAndPrepared(Long characterId, Boolean prepared);
    Optional<CharacterSpell> findByCharacterIdAndSpellId(Long characterId, Long spellId);
}
