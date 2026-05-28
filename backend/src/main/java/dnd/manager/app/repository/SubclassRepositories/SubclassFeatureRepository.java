package dnd.manager.app.repository.SubclassRepositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import dnd.manager.app.model.SubclassEntities.SubclassFeature;
import dnd.manager.app.model.SubclassEntities.SubclassFeatureId;


public interface SubclassFeatureRepository extends JpaRepository<SubclassFeature, SubclassFeatureId> {

    List<SubclassFeature> findBySubclassIdAndLevelLessThanEqual(Long subclassId, Integer level);

}