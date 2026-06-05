package dnd.manager.app.repository.SpellRepositories;
 
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import dnd.manager.app.model.SpellEntities.SpellDamageType;
import dnd.manager.app.model.SpellEntities.SpellDamageTypeId;
 
public interface SpellDamageTypeRepository extends JpaRepository<SpellDamageType, SpellDamageTypeId> {
    List<SpellDamageType> findBySpellId(Long spellId);
}
