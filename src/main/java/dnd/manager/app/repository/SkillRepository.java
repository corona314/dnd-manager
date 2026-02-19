package dnd.manager.app.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import dnd.manager.app.model.Skill;
public interface SkillRepository extends JpaRepository<Skill, Long>  {

}
