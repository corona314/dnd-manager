package dnd.manager.app.repository.BonusRepositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import dnd.manager.app.model.BonusEntities.BonusTraitObject;
import dnd.manager.app.model.BonusEntities.BonusTraitObjectId;

public interface BonusTraitObjectRepository extends JpaRepository<BonusTraitObject, BonusTraitObjectId> {

    List<BonusTraitObject> findByObjectId(Long objectId);

    List<BonusTraitObject> findByTraitId(Long traitId);

}