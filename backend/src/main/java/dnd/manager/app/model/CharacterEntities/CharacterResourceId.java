package dnd.manager.app.model.CharacterEntities;

import java.io.Serializable;
import java.util.Objects;

public class CharacterResourceId implements Serializable {
        private Long character;
        private Long classEntity;
        private String name;

        public CharacterResourceId() {}

        public Long getCharacter() { return character; }
        public void setCharacter(Long character) { this.character = character; }

        public Long getClassEntity() { return classEntity; }
        public void setClassEntity(Long classEntity) { this.classEntity = classEntity; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof CharacterResourceId)) return false;
            CharacterResourceId that = (CharacterResourceId) o;
            return Objects.equals(character, that.character) && Objects.equals(classEntity, that.classEntity) && Objects.equals(name, that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(character, classEntity, name);
        }
    }