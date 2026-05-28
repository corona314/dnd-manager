package dnd.manager.app.repository.FeatureRepositories;
 
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import dnd.manager.app.model.FeatureEntities.FeatureType;
 
public interface FeatureTypeRepository extends JpaRepository<FeatureType, Long> {
    Optional<FeatureType> findByName(String name);
}
