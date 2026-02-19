package dnd.manager.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dnd.manager.app.model.Characters;

public interface CharactersRepository extends JpaRepository<Characters, Long> {

}

