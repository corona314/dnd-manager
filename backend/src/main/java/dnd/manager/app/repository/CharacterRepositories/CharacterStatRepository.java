package dnd.manager.app.repository.CharacterRepositories;

import org.springframework.data.jpa.repository.JpaRepository;

import dnd.manager.app.model.CharacterEntities.CharacterStat;
import dnd.manager.app.model.CharacterEntities.CharacterStatId;

import java.util.List;


public interface CharacterStatRepository extends JpaRepository<CharacterStat, CharacterStatId> {

    List<CharacterStat> findByCharacterId(Long id);

    void deleteByCharacterId(Long id);

}
