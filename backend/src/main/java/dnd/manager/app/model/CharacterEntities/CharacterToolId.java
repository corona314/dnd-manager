package dnd.manager.app.model.CharacterEntities;

import java.io.Serializable;
import java.util.Objects;

public class CharacterToolId implements Serializable {
        private Long character;
        private Long item;

        public CharacterToolId() {}

        public Long getCharacter() { return character; }
        public void setCharacter(Long character) { this.character = character; }

        public Long getItem() { return item; }
        public void setItem(Long item) { this.item = item; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof CharacterToolId)) return false;
            CharacterToolId that = (CharacterToolId) o;
            return Objects.equals(character, that.character) && Objects.equals(item, that.item);
        }

        @Override
        public int hashCode() {
            return Objects.hash(character, item);
        }
    }