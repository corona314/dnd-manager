package dnd.manager.app.model.CharacterEntities;

import java.io.Serializable;
import java.util.Objects;

public class CharacterFeatureId implements Serializable {
        private Long character;
        private Long feature;

        public CharacterFeatureId() {}

        public Long getCharacter() { return character; }
        public void setCharacter(Long character) { this.character = character; }

        public Long getFeature() { return feature; }
        public void setFeature(Long feature) { this.feature = feature; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof CharacterFeatureId)) return false;
            CharacterFeatureId that = (CharacterFeatureId) o;
            return Objects.equals(character, that.character) && Objects.equals(feature, that.feature);
        }

        @Override
        public int hashCode() {
            return Objects.hash(character, feature);
        }
    }