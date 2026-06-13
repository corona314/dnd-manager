package dnd.manager.app.model.ClassEntities;

import java.io.Serializable;
import java.util.Objects;

public class ClassFeatureId implements Serializable {
        private Long classId;
        private Long featureId;

        public ClassFeatureId() {}

        public Long getClassId() { return classId; }
        public void setClassId(Long classId) { this.classId = classId; }

        public Long getFeatureId() { return featureId; }
        public void setFeatureId(Long featureId) { this.featureId = featureId; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ClassFeatureId)) return false;
            ClassFeatureId that = (ClassFeatureId) o;
            return Objects.equals(classId, that.classId) && Objects.equals(featureId, that.featureId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(classId, featureId);
        }
    }
