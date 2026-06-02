package dnd.manager.app.repository.SpellRepositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import dnd.manager.app.model.SpellEntities.Spell;
 
public interface SpellRepository extends JpaRepository<Spell, Long>, JpaSpecificationExecutor<Spell> {

}
