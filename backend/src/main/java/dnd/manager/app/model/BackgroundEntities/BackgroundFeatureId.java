package dnd.manager.app.model.BackgroundEntities;

import java.io.Serializable;
import java.util.Objects;

public class BackgroundFeatureId implements Serializable {

    private Long background;
    private Long feature;

    public BackgroundFeatureId() {}

    public BackgroundFeatureId(Long background, Long feature) {
        this.background = background;
        this.feature = feature;
    }

    public Long getBackground() { return background; }
    public void setBackground(Long background) { this.background = background; }

    public Long getFeature() { return feature; }
    public void setFeature(Long feature) { this.feature = feature; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BackgroundFeatureId)) return false;
        BackgroundFeatureId that = (BackgroundFeatureId) o;
        return Objects.equals(background, that.background) && Objects.equals(feature, that.feature);
    }

    @Override
    public int hashCode() {
        return Objects.hash(background, feature);
    }
}