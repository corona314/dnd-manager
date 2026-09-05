package dnd.manager.app.repository.CharacterRepositories;

import org.springframework.data.jpa.repository.JpaRepository;

import dnd.manager.app.model.CharacterEntities.CharacterResource;
import dnd.manager.app.model.CharacterEntities.CharacterResourceId;

public interface CharacterResourceRepository extends JpaRepository<CharacterResource, CharacterResourceId> {

}
