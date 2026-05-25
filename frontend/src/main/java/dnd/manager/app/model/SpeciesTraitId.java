package dnd.manager.app.model;

import java.io.Serializable;
import java.util.Objects;

public  class SpeciesTraitId implements Serializable {
    private Long species;
    private Long trait;

    public SpeciesTraitId() {}

    public SpeciesTraitId(Long species, Long trait) {
        this.species = species;
        this.trait = trait;
    }

    public Long getSpecies() { return species; }
    public void setSpecies(Long species) { this.species = species; }

    public Long getTrait() { return trait; }
    public void setTrait(Long trait) { this.trait = trait; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SpeciesTraitId)) return false;
        SpeciesTraitId that = (SpeciesTraitId) o;
        return Objects.equals(species, that.species) && Objects.equals(trait, that.trait);
    }

    @Override
    public int hashCode() {
        return Objects.hash(species, trait);
    }
}
