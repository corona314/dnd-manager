package dnd.manager.app.model.SubclassEntities;

import java.io.Serializable;
import java.util.Objects;

public  class SubclassSpellId implements Serializable {
    private Long subclass;
    private Long spell;

    public SubclassSpellId() {}

    public SubclassSpellId(Long subclass, Long spell) {
        this.subclass = subclass;
        this.spell = spell;
    }

    public Long getSubclass() { return subclass; }
    public void setSubclass(Long species) { this.subclass = species; }

    public Long getSpell() { return spell; }
    public void setSpell(Long spell) { this.spell = spell; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SubclassSpellId)) return false;
        SubclassSpellId that = (SubclassSpellId) o;
        return Objects.equals(subclass, that.subclass) && Objects.equals(spell, that.spell);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subclass, spell);
    }
}
