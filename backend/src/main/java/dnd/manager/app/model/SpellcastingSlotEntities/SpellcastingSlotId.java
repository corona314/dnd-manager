package dnd.manager.app.model.SpellcastingSlotEntities;

import java.io.Serializable;
import java.util.Objects;

public  class SpellcastingSlotId implements Serializable {
    private Integer casterLevel;
    private Integer spellLevel;

    public SpellcastingSlotId() {}

    public SpellcastingSlotId(Integer casterLevel, Integer spellLevel) {
        this.casterLevel = casterLevel;
        this.spellLevel = spellLevel;
    }

    public Integer getCasterLevel() { return casterLevel; }
    public void setCasterLevel(Integer casterLevel) { this.casterLevel = casterLevel; }

    public Integer getSpellLevel() { return spellLevel; }
    public void setSpellLevel(Integer spellLevel) { this.spellLevel = spellLevel; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SpellcastingSlotId)) return false;
        SpellcastingSlotId that = (SpellcastingSlotId) o;
        return Objects.equals(casterLevel, that.casterLevel) && Objects.equals(spellLevel, that.spellLevel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(casterLevel, spellLevel);
    }
}
