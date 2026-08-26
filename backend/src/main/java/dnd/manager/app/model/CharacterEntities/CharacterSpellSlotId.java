package dnd.manager.app.model.CharacterEntities;

import java.io.Serializable;
import java.util.Objects;

public class CharacterSpellSlotId implements Serializable {
        private Long character;
        private Integer spellLevel;

        public CharacterSpellSlotId() {}

        public Long getCharacter() { return character; }
        public void setCharacter(Long character) { this.character = character; }

        public Integer getSpellLevel() { return spellLevel; }
        public void setSpellLevel(Integer spellLevel) { this.spellLevel = spellLevel; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof CharacterSpellSlotId)) return false;
            CharacterSpellSlotId that = (CharacterSpellSlotId) o;
            return Objects.equals(character, that.character) && Objects.equals(spellLevel, that.spellLevel);
        }

        @Override
        public int hashCode() {
            return Objects.hash(character, spellLevel);
        }
    }