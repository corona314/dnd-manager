package dnd.manager.app.repository.BackgroundRepositories;
 
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import dnd.manager.app.model.BackgroundEntities.BackgroundTrait;
import dnd.manager.app.model.BackgroundEntities.BackgroundTraitId;
 
public interface BackgroundTraitRepository extends JpaRepository<BackgroundTrait, BackgroundTraitId> {
    List<BackgroundTrait> findByBackgroundId(Long backgroundId);
}
