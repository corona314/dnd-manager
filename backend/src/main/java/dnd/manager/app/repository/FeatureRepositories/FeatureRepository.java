package dnd.manager.app.repository.FeatureRepositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import dnd.manager.app.model.FeatureEntities.Feature;

public interface FeatureRepository extends JpaRepository<Feature, Long> {

    List<Feature> findByFeatureTypeId(Long featureTypeId);

}
