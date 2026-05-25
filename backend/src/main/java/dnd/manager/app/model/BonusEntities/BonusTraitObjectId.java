package dnd.manager.app.model.BonusEntities;

import java.io.Serializable;

public class BonusTraitObjectId implements Serializable {
    private Long traitId;
    private Long objectId;

    public BonusTraitObjectId() {}

    public BonusTraitObjectId(Long traitId, Long objectId) {
        this.traitId = traitId;
        this.objectId = objectId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BonusTraitObjectId)) return false;

        BonusTraitObjectId that = (BonusTraitObjectId) o;

        if (!traitId.equals(that.traitId)) return false;
        return objectId.equals(that.objectId);
    }
}