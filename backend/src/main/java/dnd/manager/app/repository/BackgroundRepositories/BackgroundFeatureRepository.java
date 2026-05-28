package dnd.manager.app.repository.BackgroundRepositories;
 
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import dnd.manager.app.model.BackgroundEntities.BackgroundFeature;
import dnd.manager.app.model.BackgroundEntities.BackgroundFeatureId;
 
public interface BackgroundFeatureRepository extends JpaRepository<BackgroundFeature, BackgroundFeatureId> {
    List<BackgroundFeature> findByBackgroundId(Long backgroundId);
}
