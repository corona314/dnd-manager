package dnd.manager.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dnd.manager.app.model.CharacterStat;
import dnd.manager.app.model.CharacterStatId;
import java.util.List;


public interface CharacterStatRepository extends JpaRepository<CharacterStat, CharacterStatId> {

    List<CharacterStat> findByCharacterId(Long id);

}
