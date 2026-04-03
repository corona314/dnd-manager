package dnd.manager.app.service;

import java.util.List;

import org.springframework.stereotype.Service;

import dnd.manager.app.model.BonusTraitStat;
import dnd.manager.app.repository.BonusTraitStatRepository;

@Service
public class BonusTraitStatService {

    private final BonusTraitStatRepository bonusTraitStatRepository;

    public BonusTraitStatService(BonusTraitStatRepository bonusTraitStatRepository) {
        this.bonusTraitStatRepository = bonusTraitStatRepository;
    }

    public List<BonusTraitStat> findByTraitId(Long traitId) {
        return bonusTraitStatRepository.findByTraitId(traitId);
    }

    public List<BonusTraitStat> findByStatId(Long statId) {
        return bonusTraitStatRepository.findByStatId(statId);
    }

}
