package dnd.manager.app.model.ClassEntities;

import java.io.Serializable;
import java.util.Objects;

public class ClassToolId implements Serializable {

    private Long classEntity;
    private Long item;

    public ClassToolId() {}

    public ClassToolId(Long classEntity, Long item) {
        this.classEntity = classEntity;
        this.item = item;
    }

    public Long getClassEntity() { return classEntity; }
    public void setClassEntity(Long classEntity) { this.classEntity = classEntity; }

    public Long getItem() { return item; }
    public void setItem(Long item) { this.item = item; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClassToolId)) return false;
        ClassToolId that = (ClassToolId) o;
        return Objects.equals(classEntity, that.classEntity) && Objects.equals(item, that.item);
    }

    @Override
    public int hashCode() {
        return Objects.hash(classEntity, item);
    }
}