package dnd.manager.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dnd.manager.app.model.CharacterEntity;

public interface CharacterRepository extends JpaRepository<CharacterEntity, Long> {

}
