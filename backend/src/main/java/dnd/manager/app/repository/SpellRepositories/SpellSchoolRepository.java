package dnd.manager.app.repository.SpellRepositories;
 
import org.springframework.data.jpa.repository.JpaRepository;

import dnd.manager.app.model.SpellEntities.SpellSchool;
 
public interface SpellSchoolRepository extends JpaRepository<SpellSchool, Long> {
}
