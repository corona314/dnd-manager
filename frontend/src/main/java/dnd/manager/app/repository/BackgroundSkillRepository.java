package dnd.manager.app.repository;
 
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import dnd.manager.app.model.BackgroundSkill;
import dnd.manager.app.model.BackgroundSkillId;
 
public interface BackgroundSkillRepository extends JpaRepository<BackgroundSkill, BackgroundSkillId> {
    List<BackgroundSkill> findByBackgroundId(Long backgroundId);
}
