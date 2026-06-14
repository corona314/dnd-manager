package dnd.manager.app.repository.BonusRepositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import dnd.manager.app.model.BonusEntities.BonusFeatureStat;
import dnd.manager.app.model.BonusEntities.BonusFeatureAbilityId;

public interface BonusFeatureAbilityRepository extends JpaRepository<BonusFeatureStat, BonusFeatureAbilityId> {

    List<BonusFeatureStat> findByFeatureId(Long featureId);

    List<BonusFeatureStat> findByAbilityId(Long abilityId);

}