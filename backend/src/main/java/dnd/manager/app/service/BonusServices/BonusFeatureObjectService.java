package dnd.manager.app.service.BonusServices;

import java.util.List;

import org.springframework.stereotype.Service;

import dnd.manager.app.model.BonusEntities.BonusFeatureItem;
import dnd.manager.app.repository.BonusRepositories.BonusFeatureObjectRepository;

@Service
public class BonusFeatureObjectService {

    private final BonusFeatureObjectRepository bonusFeatureObjectRepository;

    public BonusFeatureObjectService(BonusFeatureObjectRepository bonusFeatureObjectRepository) {
        this.bonusFeatureObjectRepository = bonusFeatureObjectRepository;
    }

    public List<BonusFeatureItem> findByObjectId(Long objectId) {
        return bonusFeatureObjectRepository.findByObjectId(objectId);
    }

    public List<BonusFeatureItem> findByFeatureId(Long featureId) {
        return bonusFeatureObjectRepository.findByFeatureId(featureId);
    }

}
