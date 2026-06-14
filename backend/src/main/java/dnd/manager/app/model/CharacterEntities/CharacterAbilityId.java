package dnd.manager.app.model.CharacterEntities;

import java.io.Serializable;
import java.util.Objects;

public class CharacterAbilityId implements Serializable {
        private Long character;
        private Long ability;

        public CharacterAbilityId() {}

        public Long getCharacter() { return character; }
        public void setCharacter(Long character) { this.character = character; }

        public Long getAbility() { return ability; }
        public void setStat(Long ability) { this.ability = ability; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof CharacterAbilityId)) return false;
            CharacterAbilityId that = (CharacterAbilityId) o;
            return Objects.equals(character, that.character) && Objects.equals(ability, that.ability);
        }

        @Override
        public int hashCode() {
            return Objects.hash(character, ability);
        }
    }