package dnd.manager.app.model.ClassEntities;

import java.io.Serializable;
import java.util.Objects;

public class ClassStartingMoneyId implements Serializable {
        private Long classEntity;
        private String optionGroup;

        public ClassStartingMoneyId() {}

        public Long getClassEntity() { return classEntity; }
        public void setClassEntity(Long classId) { this.classEntity = classId; }

        public String getOptionGroup() { return optionGroup; }
        public void setOptionGroup(String optionGroupId) { this.optionGroup = optionGroupId; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ClassStartingMoneyId)) return false;
            ClassStartingMoneyId that = (ClassStartingMoneyId) o;
            return Objects.equals(classEntity, that.classEntity) && Objects.equals(optionGroup, that.optionGroup);
        }

        @Override
        public int hashCode() {
            return Objects.hash(classEntity, optionGroup);
        }
    }