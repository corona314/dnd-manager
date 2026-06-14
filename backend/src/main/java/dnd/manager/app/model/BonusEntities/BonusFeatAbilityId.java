package dnd.manager.app.model.BonusEntities;

import java.io.Serializable;
import java.util.Objects;

public class BonusFeatAbilityId implements Serializable {
        private Long feat;
        private Long ability;

        public BonusFeatAbilityId() {}

        public Long getFeat() { return feat; }
        public void setFeat(Long feat) { this.feat = feat; }

        public Long getAbility() { return ability; }
        public void setStat(Long ability) { this.ability = ability; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof BonusFeatAbilityId)) return false;
            BonusFeatAbilityId that = (BonusFeatAbilityId) o;
            return Objects.equals(feat, that.feat) && Objects.equals(ability, that.ability);
        }

        @Override
        public int hashCode() {
            return Objects.hash(feat, ability);
        }
    }
