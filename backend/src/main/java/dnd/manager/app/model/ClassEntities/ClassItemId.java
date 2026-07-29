package dnd.manager.app.model.ClassEntities;

import java.io.Serializable;
import java.util.Objects;

public class ClassItemId implements Serializable {
        private Long classEntity;
        private Long item;

        public ClassItemId() {}

        public Long getClassEntity() { return classEntity; }
        public void setClassEntity(Long classId) { this.classEntity = classId; }

        public Long getItem() { return item; }
        public void setItem(Long itemId) { this.item = itemId; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ClassItemId)) return false;
            ClassItemId that = (ClassItemId) o;
            return Objects.equals(classEntity, that.classEntity) && Objects.equals(item, that.item);
        }

        @Override
        public int hashCode() {
            return Objects.hash(classEntity, item);
        }
    }
