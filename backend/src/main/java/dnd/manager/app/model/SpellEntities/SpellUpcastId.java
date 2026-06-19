package dnd.manager.app.model.SpellEntities;

import java.io.Serializable;
import java.util.Objects;

public class SpellUpcastId implements Serializable {
    private Long spell;
    private int level;

    public SpellUpcastId() {}

    public Long getSpell() { return spell; }
    public void setSpell(Long spellId) { this.spell = spellId; }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SpellUpcastId)) return false;
        SpellUpcastId that = (SpellUpcastId) o;
        return level == that.level && Objects.equals(spell, that.spell);
    }

    @Override
    public int hashCode() {
        return Objects.hash(spell, level);
    }
}