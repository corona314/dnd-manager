package dnd.manager.app.model.BonusEntities;

import java.io.Serializable;

public class BonusFeatureStatId implements Serializable {
    private Long featureId;
    private Long statId;

    public BonusFeatureStatId() {}

    public BonusFeatureStatId(Long featureId, Long statId) {
        this.featureId = featureId;
        this.statId = statId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BonusFeatureStatId)) return false;

        BonusFeatureStatId that = (BonusFeatureStatId) o;

        if (!featureId.equals(that.featureId)) return false;
        return statId.equals(that.statId);
    }
}