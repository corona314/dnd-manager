package dnd.manager.app.repository.CharacterRepositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import dnd.manager.app.model.CharacterEntities.CharacterSkill;
import dnd.manager.app.model.CharacterEntities.CharacterSkillId;

public interface CharacterSkillRepository extends JpaRepository<CharacterSkill, CharacterSkillId> {

    List<CharacterSkill> findByCharacterId(Long id);
}
