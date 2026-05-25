package dnd.manager.app.repository.CharacterRepositories;

import org.springframework.data.jpa.repository.JpaRepository;

import dnd.manager.app.model.CharacterEntities.CharacterResource;

public interface CharacterResourceRepository extends JpaRepository<CharacterResource, Long> {

}
