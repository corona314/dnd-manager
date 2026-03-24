package dnd.manager.app.repository;
 
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import dnd.manager.app.model.Background;
 
public interface BackgroundRepository extends JpaRepository<Background, Long> {
    Optional<Background> findByName(String name);
}
