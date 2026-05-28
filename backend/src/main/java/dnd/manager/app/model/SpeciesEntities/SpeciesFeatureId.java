package dnd.manager.app.model.SpeciesEntities;

import java.io.Serializable;
import java.util.Objects;

public  class SpeciesFeatureId implements Serializable {
    private Long species;
    private Long feature;

    public SpeciesFeatureId() {}

    public SpeciesFeatureId(Long species, Long feature) {
        this.species = species;
        this.feature = feature;
    }

    public Long getSpecies() { return species; }
    public void setSpecies(Long species) { this.species = species; }

    public Long getFeature() { return feature; }
    public void setFeature(Long feature) { this.feature = feature; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SpeciesFeatureId)) return false;
        SpeciesFeatureId that = (SpeciesFeatureId) o;
        return Objects.equals(species, that.species) && Objects.equals(feature, that.feature);
    }

    @Override
    public int hashCode() {
        return Objects.hash(species, feature);
    }
}
