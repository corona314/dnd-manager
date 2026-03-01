package dnd.manager.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dnd.manager.app.model.BonusTraitStat;
import dnd.manager.app.model.BonusTraitStatId;

public interface BonusTraitStatRepository extends JpaRepository<BonusTraitStat, BonusTraitStatId> {

}