package dnd.manager.app.repository.SubclassRepositories;

import org.springframework.data.jpa.repository.JpaRepository;
import dnd.manager.app.model.SubclassEntities.SubclassSpell;
import dnd.manager.app.model.SubclassEntities.SubclassSpellId;


public interface SubclassSpellRepository extends JpaRepository<SubclassSpell, SubclassSpellId> {

}