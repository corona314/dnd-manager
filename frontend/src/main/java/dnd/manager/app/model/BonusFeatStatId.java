package dnd.manager.app.model;

import java.io.Serializable;
import java.util.Objects;

public class BonusFeatStatId implements Serializable {
        private Long feat;
        private Long stat;

        public BonusFeatStatId() {}

        public Long getFeat() { return feat; }
        public void setFeat(Long feat) { this.feat = feat; }

        public Long getStat() { return stat; }
        public void setStat(Long stat) { this.stat = stat; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof BonusFeatStatId)) return false;
            BonusFeatStatId that = (BonusFeatStatId) o;
            return Objects.equals(feat, that.feat) && Objects.equals(stat, that.stat);
        }

        @Override
        public int hashCode() {
            return Objects.hash(feat, stat);
        }
    }
