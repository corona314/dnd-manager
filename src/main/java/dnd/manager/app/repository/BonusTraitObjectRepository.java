package dnd.manager.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import dnd.manager.app.model.BonusTraitObject;
import dnd.manager.app.model.BonusTraitObjectId;

public interface BonusTraitObjectRepository extends JpaRepository<BonusTraitObject, BonusTraitObjectId> {

    List<BonusTraitObject> findByObjectId(Long objectId);

    List<BonusTraitObject> findByTraitId(Long traitId);

}