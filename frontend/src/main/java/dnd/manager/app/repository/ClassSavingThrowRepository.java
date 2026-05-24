package dnd.manager.app.repository;
 
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import dnd.manager.app.model.ClassSavingThrow;
import dnd.manager.app.model.ClassSavingThrowId;
 
public interface ClassSavingThrowRepository extends JpaRepository<ClassSavingThrow, ClassSavingThrowId> {
    List<ClassSavingThrow> findByClassEntityId(Long classId);
}

