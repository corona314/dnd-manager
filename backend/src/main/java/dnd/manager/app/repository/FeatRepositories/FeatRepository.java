package dnd.manager.app.repository.FeatRepositories;
 
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import dnd.manager.app.model.Feat;
 
public interface FeatRepository extends JpaRepository<Feat, Long>, JpaSpecificationExecutor<Feat> {
    Optional<Feat> findByName(String name);
    List<Feat> findByFeatCategory(String featCategory);
}
