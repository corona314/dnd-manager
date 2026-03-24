package dnd.manager.app.model;

import java.io.Serializable;
import java.util.Objects;

public  class SubclassTraitId implements Serializable {
    private Long subclass;
    private Long trait;

    public SubclassTraitId() {}

    public SubclassTraitId(Long subclass, Long trait) {
        this.subclass = subclass;
        this.trait = trait;
    }

    public Long getSubclass() { return subclass; }
    public void setSubclass(Long specie) { this.subclass = specie; }

    public Long getTrait() { return trait; }
    public void setTrait(Long trait) { this.trait = trait; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SubclassTraitId)) return false;
        SubclassTraitId that = (SubclassTraitId) o;
        return Objects.equals(subclass, that.subclass) && Objects.equals(trait, that.trait);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subclass, trait);
    }
}
