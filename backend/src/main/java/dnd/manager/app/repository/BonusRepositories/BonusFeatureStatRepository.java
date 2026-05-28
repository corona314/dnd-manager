package dnd.manager.app.repository.BonusRepositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import dnd.manager.app.model.BonusEntities.BonusFeatureStat;
import dnd.manager.app.model.BonusEntities.BonusFeatureStatId;

public interface BonusFeatureStatRepository extends JpaRepository<BonusFeatureStat, BonusFeatureStatId> {

    List<BonusFeatureStat> findByFeatureId(Long featureId);

    List<BonusFeatureStat> findByStatId(Long statId);

}