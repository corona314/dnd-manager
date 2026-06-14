package dnd.manager.app.model.ClassEntities;

import java.io.Serializable;
import java.util.Objects;

public class ClassSkillId implements Serializable {
        private Long classEntity;
        private Long skill;

        public ClassSkillId() {}

        public Long getClassEntity() { return classEntity; }
        public void setClassEntity(Long classId) { this.classEntity = classId; }

        public Long getSkill() { return skill; }
        public void setSkill(Long skillId) { this.skill = skillId; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ClassSkillId)) return false;
            ClassSkillId that = (ClassSkillId) o;
            return Objects.equals(classEntity, that.classEntity) && Objects.equals(skill, that.skill);
        }

        @Override
        public int hashCode() {
            return Objects.hash(classEntity, skill);
        }
    }
