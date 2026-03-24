package dnd.manager.app.model;

import java.io.Serializable;

public class BonusTraitStatId implements Serializable {
    private Long traitId;
    private Long statId;

    public BonusTraitStatId() {}

    public BonusTraitStatId(Long traitId, Long statId) {
        this.traitId = traitId;
        this.statId = statId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BonusTraitStatId)) return false;

        BonusTraitStatId that = (BonusTraitStatId) o;

        if (!traitId.equals(that.traitId)) return false;
        return statId.equals(that.statId);
    }
}