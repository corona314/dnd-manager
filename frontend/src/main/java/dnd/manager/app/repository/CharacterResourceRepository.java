package dnd.manager.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dnd.manager.app.model.CharacterResource;

public interface CharacterResourceRepository extends JpaRepository<CharacterResource, Long> {

}
