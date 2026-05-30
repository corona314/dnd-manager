package dnd.manager.app.repository.BackgroundRepositories;
 
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import dnd.manager.app.model.BackgroundEntities.BackgroundFeat;
import dnd.manager.app.model.BackgroundEntities.BackgroundFeatId;
 
public interface BackgroundFeatRepository extends JpaRepository<BackgroundFeat, BackgroundFeatId> {
    List<BackgroundFeat> findByBackgroundId(Long backgroundId);
}
