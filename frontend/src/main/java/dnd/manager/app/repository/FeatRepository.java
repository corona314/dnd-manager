package dnd.manager.app.repository;
 
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import dnd.manager.app.model.Feat;
 
public interface FeatRepository extends JpaRepository<Feat, Long> {
    Optional<Feat> findByName(String name);
    List<Feat> findByFeatCategory(String featCategory);
}
