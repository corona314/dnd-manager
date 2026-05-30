package dnd.manager.app.repository.BackgroundRepositories;
 
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import dnd.manager.app.model.BackgroundEntities.BackgroundStat;
import dnd.manager.app.model.BackgroundEntities.BackgroundStatId;
 
public interface BackgroundStatRepository extends JpaRepository<BackgroundStat, BackgroundStatId> {
    List<BackgroundStat> findByBackgroundId(Long backgroundId);
}
