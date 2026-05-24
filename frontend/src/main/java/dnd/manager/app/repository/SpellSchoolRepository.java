package dnd.manager.app.repository;
 
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import dnd.manager.app.model.SpellSchool;
 
public interface SpellSchoolRepository extends JpaRepository<SpellSchool, Long> {
    Optional<SpellSchool> findByName(String name);
}
