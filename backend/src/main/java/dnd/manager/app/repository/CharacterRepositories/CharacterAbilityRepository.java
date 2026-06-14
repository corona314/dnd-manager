package dnd.manager.app.repository.CharacterRepositories;

import org.springframework.data.jpa.repository.JpaRepository;

import dnd.manager.app.model.CharacterEntities.CharacterAbility;
import dnd.manager.app.model.CharacterEntities.CharacterAbilityId;

import java.util.List;


public interface CharacterAbilityRepository extends JpaRepository<CharacterAbility, CharacterAbilityId> {

    List<CharacterAbility> findByCharacterId(Long id);

    void deleteByCharacterId(Long id);

}
