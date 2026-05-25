package dnd.manager.app.repository.SubclassRepositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import dnd.manager.app.model.SubclassEntities.SubclassTrait;
import dnd.manager.app.model.SubclassEntities.SubclassTraitId;


public interface SubclassTraitRepository extends JpaRepository<SubclassTrait, SubclassTraitId> {

    List<SubclassTrait> findBySubclassIdAndLevelLessThanEqual(Long subclassId, Integer level);

}