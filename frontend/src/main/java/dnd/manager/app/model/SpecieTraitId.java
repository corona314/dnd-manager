package dnd.manager.app.model;

import java.io.Serializable;
import java.util.Objects;

public  class SpecieTraitId implements Serializable {
    private Long specie;
    private Long trait;

    public SpecieTraitId() {}

    public SpecieTraitId(Long specie, Long trait) {
        this.specie = specie;
        this.trait = trait;
    }

    public Long getSpecie() { return specie; }
    public void setSpecie(Long specie) { this.specie = specie; }

    public Long getTrait() { return trait; }
    public void setTrait(Long trait) { this.trait = trait; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SpecieTraitId)) return false;
        SpecieTraitId that = (SpecieTraitId) o;
        return Objects.equals(specie, that.specie) && Objects.equals(trait, that.trait);
    }

    @Override
    public int hashCode() {
        return Objects.hash(specie, trait);
    }
}
