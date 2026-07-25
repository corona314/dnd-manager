package dnd.manager.app.dto.CharacterDto;

/*
    Clase con atributos que puede editar el usuario
*/

public record CharacterPatchDto(
    String name,
    Integer currentHp,
    Integer maxHp,
    Integer money,
    Integer experience,
    Integer walkSpeed,
    Integer flySpeed,
    Long speciesId,
    Long classId,
    Long subclassId,
    Long backgroundId

) {}
