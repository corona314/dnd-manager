package dnd.manager.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import dnd.manager.app.model.ClassEntity;

public interface ClassRepository extends JpaRepository<ClassEntity, Long> {

}
