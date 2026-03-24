package dnd.manager.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import dnd.manager.app.model.SubclassTrait;
import dnd.manager.app.model.SubclassTraitId;


public interface SubclassTraitRepository extends JpaRepository<SubclassTrait, SubclassTraitId> {

    List<SubclassTrait> findBySubclassIdAndLevelLessThanEqual(Long subclassId, Integer level);

}