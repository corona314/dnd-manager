package dnd.manager.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import dnd.manager.app.model.BonusTraitStat;
import dnd.manager.app.model.BonusTraitStatId;

public interface BonusTraitStatRepository extends JpaRepository<BonusTraitStat, BonusTraitStatId> {

    List<BonusTraitStat> findByTraitId(Long traitId);

    List<BonusTraitStat> findByStatId(Long statId);

}