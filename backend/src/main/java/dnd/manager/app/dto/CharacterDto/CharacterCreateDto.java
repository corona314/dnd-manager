package dnd.manager.app.dto.CharacterDto;

import jakarta.validation.constraints.Min;

/*
    Clase mínima necesaria para que exista un Character
*/
public record CharacterCreateDto(
    Long userId,
    String name,
    @Min(1) Integer level
) {}
