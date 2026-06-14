package dnd.manager.app.model.ClassEntities;

import java.io.Serializable;
import java.util.Objects;

public class ClassFeatureId implements Serializable {
        private Long classEntity;
        private Long feature;

        public ClassFeatureId() {}

        public Long getClassEntity() { return classEntity; }
        public void setClassEntity(Long classId) { this.classEntity = classId; }

        public Long getFeature() { return feature; }
        public void setFeature(Long featureId) { this.feature = featureId; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ClassFeatureId)) return false;
            ClassFeatureId that = (ClassFeatureId) o;
            return Objects.equals(classEntity, that.classEntity) && Objects.equals(feature, that.feature);
        }

        @Override
        public int hashCode() {
            return Objects.hash(classEntity, feature);
        }
    }
