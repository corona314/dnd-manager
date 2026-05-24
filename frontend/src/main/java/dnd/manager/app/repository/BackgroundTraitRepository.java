package dnd.manager.app.repository;
 
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import dnd.manager.app.model.BackgroundTrait;
import dnd.manager.app.model.BackgroundTraitId;
 
public interface BackgroundTraitRepository extends JpaRepository<BackgroundTrait, BackgroundTraitId> {
    List<BackgroundTrait> findByBackgroundId(Long backgroundId);
}
