package dnd.manager.app.repository.SpellRepositories;
 
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import dnd.manager.app.model.SpellEntities.Spell;
 
public interface SpellRepository extends JpaRepository<Spell, Long> {
    Optional<Spell> findByName(String name);
    List<Spell> findByLevel(Integer level);
    List<Spell> findBySchoolId(Long schoolId);
    List<Spell> findByConcentration(Boolean concentration);
    List<Spell> findByRitual(Boolean ritual);
}
