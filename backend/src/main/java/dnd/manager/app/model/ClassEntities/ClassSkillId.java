package dnd.manager.app.model.ClassEntities;

import java.io.Serializable;
import java.util.Objects;

public class ClassSkillId implements Serializable {
        private Long classId;
        private Long skillId;

        public ClassSkillId() {}

        public Long getClassId() { return classId; }
        public void setClassId(Long classId) { this.classId = classId; }

        public Long getSkillId() { return skillId; }
        public void setSkillId(Long skillId) { this.skillId = skillId; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ClassSkillId)) return false;
            ClassSkillId that = (ClassSkillId) o;
            return Objects.equals(classId, that.classId) && Objects.equals(skillId, that.skillId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(classId, skillId);
        }
    }
