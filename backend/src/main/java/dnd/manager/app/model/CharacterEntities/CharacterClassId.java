package dnd.manager.app.model.CharacterEntities;

import java.io.Serializable;
import java.util.Objects;

public class CharacterClassId implements Serializable {

    private Long character;
    private Long classEntity;

    public CharacterClassId() {}

    public CharacterClassId(Long character, Long classEntity) {
        this.character = character;
        this.classEntity = classEntity;
    }

    public Long getCharacter() {
        return character;
    }

    public void setCharacter(Long character) {
        this.character = character;
    }

    public Long getClassEntity() {
        return classEntity;
    }

    public void setClassEntity(Long classEntity) {
        this.classEntity = classEntity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CharacterClassId that)) return false;

        return Objects.equals(character, that.character)
            && Objects.equals(classEntity, that.classEntity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(character, classEntity);
    }
}