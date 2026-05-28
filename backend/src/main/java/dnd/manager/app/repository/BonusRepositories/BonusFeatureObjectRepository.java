package dnd.manager.app.repository.BonusRepositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import dnd.manager.app.model.BonusEntities.BonusFeatureItem;
import dnd.manager.app.model.BonusEntities.BonusFeatureItemId;

public interface BonusFeatureObjectRepository extends JpaRepository<BonusFeatureItem, BonusFeatureItemId> {

    List<BonusFeatureItem> findByObjectId(Long objectId);

    List<BonusFeatureItem> findByFeatureId(Long featureId);

}