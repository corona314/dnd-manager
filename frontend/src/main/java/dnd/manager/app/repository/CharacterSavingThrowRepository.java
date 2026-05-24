package dnd.manager.app.repository;
 
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import dnd.manager.app.model.CharacterSavingThrow;
import dnd.manager.app.model.CharacterSavingThrowId;
 
public interface CharacterSavingThrowRepository extends JpaRepository<CharacterSavingThrow, CharacterSavingThrowId> {
    List<CharacterSavingThrow> findByCharacterId(Long characterId);
}
