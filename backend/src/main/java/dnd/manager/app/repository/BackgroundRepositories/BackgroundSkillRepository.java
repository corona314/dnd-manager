package dnd.manager.app.repository.BackgroundRepositories;
 
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import dnd.manager.app.model.BackgroundEntities.BackgroundSkill;
import dnd.manager.app.model.BackgroundEntities.BackgroundSkillId;
 
public interface BackgroundSkillRepository extends JpaRepository<BackgroundSkill, BackgroundSkillId> {
    List<BackgroundSkill> findByBackgroundId(Long backgroundId);
}
