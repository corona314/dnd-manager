package dnd.manager.app.repository.BonusRepositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import dnd.manager.app.model.BonusEntities.BonusFeatStat;
import dnd.manager.app.model.BonusEntities.BonusFeatStatId;



public interface BonusFeatStatRepository extends JpaRepository<BonusFeatStat, BonusFeatStatId> {

    List<BonusFeatStat> findByFeatId(Long featId);
    List<BonusFeatStat> findByStatId(Long statId);

}