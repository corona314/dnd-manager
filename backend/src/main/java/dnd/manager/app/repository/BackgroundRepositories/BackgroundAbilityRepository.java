package dnd.manager.app.repository.BackgroundRepositories;
 
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import dnd.manager.app.model.BackgroundEntities.BackgroundStat;
import dnd.manager.app.model.BackgroundEntities.BackgroundAbilityId;
 
public interface BackgroundAbilityRepository extends JpaRepository<BackgroundStat, BackgroundAbilityId> {
    List<BackgroundStat> findByBackgroundId(Long backgroundId);
}
