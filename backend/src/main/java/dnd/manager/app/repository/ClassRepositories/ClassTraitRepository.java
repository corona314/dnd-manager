package dnd.manager.app.repository.ClassRepositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import dnd.manager.app.model.ClassEntities.ClassTrait;
import dnd.manager.app.model.ClassEntities.ClassTraitId;

public interface ClassTraitRepository extends JpaRepository<ClassTrait, ClassTraitId> {

    List<ClassTrait> findByClassEntityIdAndLevelLessThanEqual(Long classId, Integer level);

}
