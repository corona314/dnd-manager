package dnd.manager.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dnd.manager.app.model.CharacterSkill;

public interface CharacterSkillRepository extends JpaRepository<CharacterSkill, Long> {

}
