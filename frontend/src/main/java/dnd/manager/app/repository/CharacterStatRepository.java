package dnd.manager.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dnd.manager.app.model.CharacterStat;

public interface CharacterStatRepository extends JpaRepository<CharacterStat, Long> {

}
