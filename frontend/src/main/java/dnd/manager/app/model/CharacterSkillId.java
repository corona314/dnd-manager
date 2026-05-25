package dnd.manager.app.model;

import java.io.Serializable;
import java.util.Objects;

public class CharacterSkillId implements Serializable {
        private Long character;
        private Long skill;

        public CharacterSkillId() {}

        public Long getCharacter() { return character; }
        public void setCharacter(Long character) { this.character = character; }

        public Long getSkill() { return skill; }
        public void setSkill(Long skill) { this.skill = skill; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof CharacterSkillId)) return false;
            CharacterSkillId that = (CharacterSkillId) o;
            return Objects.equals(character, that.character) && Objects.equals(skill, that.skill);
        }

        @Override
        public int hashCode() {
            return Objects.hash(character, skill);
        }
    }