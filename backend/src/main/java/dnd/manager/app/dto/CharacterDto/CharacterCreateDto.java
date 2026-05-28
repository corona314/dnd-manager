package dnd.manager.app.dto.CharacterDto;

/*
    Clase mínima necesaria para que exista un Character
*/
public record CharacterCreateDto(
    Long userId,
    String name
) {}
