package dnd.manager.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dnd.manager.app.model.BonusTraitObject;
import dnd.manager.app.model.BonusTraitObjectId;

public interface BonusTraitObjectRepository extends JpaRepository<BonusTraitObject, BonusTraitObjectId> {

}