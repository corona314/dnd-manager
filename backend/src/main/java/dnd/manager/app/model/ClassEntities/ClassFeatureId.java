package dnd.manager.app.model.ClassEntities;

import java.io.Serializable;

public class ClassFeatureId implements Serializable {
    private Long classId;
    private Long featureId;

    public ClassFeatureId() {}

    public ClassFeatureId(Long classId, Long featureId) {
        this.classId = classId;
        this.featureId = featureId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClassFeatureId)) return false;

        ClassFeatureId that = (ClassFeatureId) o;

        if (!classId.equals(that.classId)) return false;
        return featureId.equals(that.featureId);
    }


}
