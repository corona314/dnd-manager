package dnd.manager.app.service.BonusServices;

import java.util.List;

import org.springframework.stereotype.Service;

import dnd.manager.app.model.BonusEntities.BonusFeatureStat;
import dnd.manager.app.repository.BonusRepositories.BonusFeatureStatRepository;

@Service
public class BonusFeatureStatService {

    private final BonusFeatureStatRepository bonusFeatureStatRepository;

    public BonusFeatureStatService(BonusFeatureStatRepository bonusFeatureStatRepository) {
        this.bonusFeatureStatRepository = bonusFeatureStatRepository;
    }

    public List<BonusFeatureStat> findByFeatureId(Long featureId) {
        return bonusFeatureStatRepository.findByFeatureId(featureId);
    }

    public List<BonusFeatureStat> findByStatId(Long statId) {
        return bonusFeatureStatRepository.findByStatId(statId);
    }

}
