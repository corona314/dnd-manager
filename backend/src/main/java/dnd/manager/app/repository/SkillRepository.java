package dnd.manager.app.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import dnd.manager.app.model.Skill;

public interface SkillRepository extends JpaRepository<Skill, Long> {
    List<Skill> findByAbilityId(Long abilityId);
}
