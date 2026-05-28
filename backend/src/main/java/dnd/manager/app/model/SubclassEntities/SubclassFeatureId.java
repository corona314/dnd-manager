package dnd.manager.app.model.SubclassEntities;

import java.io.Serializable;
import java.util.Objects;

public  class SubclassFeatureId implements Serializable {
    private Long subclass;
    private Long feature;

    public SubclassFeatureId() {}

    public SubclassFeatureId(Long subclass, Long feature) {
        this.subclass = subclass;
        this.feature = feature;
    }

    public Long getSubclass() { return subclass; }
    public void setSubclass(Long species) { this.subclass = species; }

    public Long getFeature() { return feature; }
    public void setFeature(Long feature) { this.feature = feature; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SubclassFeatureId)) return false;
        SubclassFeatureId that = (SubclassFeatureId) o;
        return Objects.equals(subclass, that.subclass) && Objects.equals(feature, that.feature);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subclass, feature);
    }
}
