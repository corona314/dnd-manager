package dnd.manager.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import dnd.manager.app.model.SubclassTrait;


public interface SubclassTraitRepository extends JpaRepository<SubclassTrait, Long> {

    List<SubclassTrait> findBySubclassIdAndLevelLessThanEqual(Long subclassId, Integer level);

}