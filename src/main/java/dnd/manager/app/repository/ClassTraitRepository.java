package dnd.manager.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import dnd.manager.app.model.ClassTrait;
import dnd.manager.app.model.ClassTraitId;

public interface ClassTraitRepository extends JpaRepository<ClassTrait, ClassTraitId> {

    List<ClassTrait> findByClassEntityIdAndLevelLessThanEqual(Long classId, Integer level);

}
