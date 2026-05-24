package dnd.manager.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import dnd.manager.app.model.BonusFeatStat;
import dnd.manager.app.model.BonusFeatStatId;



public interface BonusFeatStatRepository extends JpaRepository<BonusFeatStat, BonusFeatStatId> {

    List<BonusFeatStat> findByFeatId(Long featId);
    List<BonusFeatStat> findByStatId(Long statId);

}