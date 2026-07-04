package dnd.manager.app.repository.BackgroundRepositories;
 
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import dnd.manager.app.model.BackgroundEntities.Background;
 
public interface BackgroundRepository extends JpaRepository<Background, Long>, JpaSpecificationExecutor<Background> {
    Optional<Background> findByName(String name);
}
