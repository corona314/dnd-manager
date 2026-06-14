package dnd.manager.app.repository.SpellRepositories;
 
import org.springframework.data.jpa.repository.JpaRepository;

import dnd.manager.app.model.SpellEntities.SpellDamageType;
import dnd.manager.app.model.SpellEntities.SpellDamageTypeId;
 
public interface SpellDamageTypeRepository extends JpaRepository<SpellDamageType, SpellDamageTypeId> {
}
