package dnd.manager.app.model.BonusEntities;

import java.io.Serializable;

public class BonusFeatureItemId implements Serializable {
    private Long featureId;
    private Long objectId;

    public BonusFeatureItemId() {}

    public BonusFeatureItemId(Long featureId, Long objectId) {
        this.featureId = featureId;
        this.objectId = objectId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BonusFeatureItemId)) return false;

        BonusFeatureItemId that = (BonusFeatureItemId) o;

        if (!featureId.equals(that.featureId)) return false;
        return objectId.equals(that.objectId);
    }
}