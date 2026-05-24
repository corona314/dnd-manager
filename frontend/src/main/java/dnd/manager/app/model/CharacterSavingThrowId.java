package dnd.manager.app.model;

import java.io.Serializable;
import java.util.Objects;

public class CharacterSavingThrowId implements Serializable {
        private Long character;
        private Long stat;

        public CharacterSavingThrowId() {}

        public Long getCharacter() { return character; }
        public void setCharacter(Long character) { this.character = character; }

        public Long getStat() { return stat; }
        public void setStat(Long stat) { this.stat = stat; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof CharacterSavingThrowId)) return false;
            CharacterSavingThrowId that = (CharacterSavingThrowId) o;
            return Objects.equals(character, that.character) && Objects.equals(stat, that.stat);
        }

        @Override
        public int hashCode() {
            return Objects.hash(character, stat);
        }
    }