package dnd.manager.app.model.FeatureEntities;

import java.io.Serializable;
import java.util.Objects;

public class FeatureChoiceId implements Serializable {

    private Long parentFeature;
    private Long choice;

    public FeatureChoiceId() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FeatureChoiceId that)) return false;
        return Objects.equals(parentFeature, that.parentFeature)
                && Objects.equals(choice, that.choice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(parentFeature, choice);
    }
}