package dnd.manager.app.repository.ClassRepositories;
 
import org.springframework.data.jpa.repository.JpaRepository;

import dnd.manager.app.model.ClassEntities.ClassSavingThrow;
import dnd.manager.app.model.ClassEntities.ClassSavingThrowId;
 
public interface ClassSavingThrowRepository extends JpaRepository<ClassSavingThrow, ClassSavingThrowId> {

}

