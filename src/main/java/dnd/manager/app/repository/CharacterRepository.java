package dnd.manager.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dnd.manager.app.model.Characters;

public interface CharacterRepository extends JpaRepository<Characters, Long> {

}

