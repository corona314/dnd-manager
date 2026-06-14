package dnd.manager.app.service.BonusServices;

import java.util.List;

import org.springframework.stereotype.Service;

import dnd.manager.app.model.BonusEntities.BonusFeatureStat;
import dnd.manager.app.repository.BonusRepositories.BonusFeatureAbilityRepository;

@Service
public class BonusFeatureStatService {

    private final BonusFeatureAbilityRepository bonusFeatureAbilityRepository;

    public BonusFeatureStatService(BonusFeatureAbilityRepository bonusFeatureAbilityRepository) {
        this.bonusFeatureAbilityRepository = bonusFeatureAbilityRepository;
    }

    public List<BonusFeatureStat> findByFeatureId(Long featureId) {
        return bonusFeatureAbilityRepository.findByFeatureId(featureId);
    }

    public List<BonusFeatureStat> findByAbilityId(Long abilityId) {
        return bonusFeatureAbilityRepository.findByAbilityId(abilityId);
    }

}
