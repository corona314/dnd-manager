package dnd.manager.app.repository.BonusRepositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import dnd.manager.app.model.BonusEntities.BonusFeatAbility;
import dnd.manager.app.model.BonusEntities.BonusFeatAbilityId;



public interface BonusFeatAbilityRepository extends JpaRepository<BonusFeatAbility, BonusFeatAbilityId> {

    List<BonusFeatAbility> findByFeatId(Long featId);
    List<BonusFeatAbility> findByAbilityId(Long abilityId);

}