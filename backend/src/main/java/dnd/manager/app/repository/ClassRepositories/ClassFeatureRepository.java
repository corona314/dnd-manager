package dnd.manager.app.repository.ClassRepositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import dnd.manager.app.model.ClassEntities.ClassFeature;
import dnd.manager.app.model.ClassEntities.ClassFeatureId;
import dnd.manager.app.model.ClassEntities.ClassSpell;

public interface ClassFeatureRepository extends JpaRepository<ClassFeature, ClassFeatureId> {

    List<ClassSpell> findByClassEntityIdAndLevelLessThanEqual(Long classId, Integer level);

}
