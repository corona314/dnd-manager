package dnd.manager.app.repository.ClassRepositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import dnd.manager.app.model.ClassEntities.ClassResource;
import dnd.manager.app.model.ClassEntities.ClassResourceId;

public interface ClassResourceRepository extends JpaRepository<ClassResource, ClassResourceId> {

    List<ClassResource> findByClassEntityIdAndLevel(Long classId, Integer level);
}
