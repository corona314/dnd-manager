package dnd.manager.app.service;

import java.util.List;

import org.springframework.stereotype.Service;

import dnd.manager.app.model.BonusTraitObject;
import dnd.manager.app.repository.BonusTraitObjectRepository;

@Service
public class BonusTraitObjectService {

    private final BonusTraitObjectRepository bonusTraitObjectRepository;

    public BonusTraitObjectService(BonusTraitObjectRepository bonusTraitObjectRepository) {
        this.bonusTraitObjectRepository = bonusTraitObjectRepository;
    }

    public List<BonusTraitObject> findByObjectId(Long objectId) {
        return bonusTraitObjectRepository.findByObjectId(objectId);
    }

    public List<BonusTraitObject> findByTraitId(Long traitId) {
        return bonusTraitObjectRepository.findByTraitId(traitId);
    }

}
