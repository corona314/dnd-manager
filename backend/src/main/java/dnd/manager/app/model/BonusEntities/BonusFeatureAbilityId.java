package dnd.manager.app.model.BonusEntities;

import java.io.Serializable;

public class BonusFeatureAbilityId implements Serializable {
    private Long featureId;
    private Long abilityId;

    public BonusFeatureAbilityId() {}

    public BonusFeatureAbilityId(Long featureId, Long abilityId) {
        this.featureId = featureId;
        this.abilityId = abilityId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BonusFeatureAbilityId)) return false;

        BonusFeatureAbilityId that = (BonusFeatureAbilityId) o;

        if (!featureId.equals(that.featureId)) return false;
        return abilityId.equals(that.abilityId);
    }
}