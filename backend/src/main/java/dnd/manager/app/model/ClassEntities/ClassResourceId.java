package dnd.manager.app.model.ClassEntities;

import java.io.Serializable;
import java.util.Objects;

public class ClassResourceId implements Serializable {
        private Long classEntity;
        private String name;
        private Integer level;

        public ClassResourceId() {}

        public Long getClassEntity() { return classEntity; }
        public void setClassEntity(Long classId) { this.classEntity = classId; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ClassResourceId)) return false;
            ClassResourceId that = (ClassResourceId) o;
            return Objects.equals(classEntity, that.classEntity) && Objects.equals(name, that.name) && Objects.equals(level, that.level);
        }

        @Override
        public int hashCode() {
            return Objects.hash(classEntity, name, level);
        }
    }
