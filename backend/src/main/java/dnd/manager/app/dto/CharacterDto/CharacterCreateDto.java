package dnd.manager.app.dto.CharacterDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/*
    Clase mínima necesaria para que exista un Character
*/
public record CharacterCreateDto(
    @NotBlank @NotNull Long userId,
    @NotBlank @NotNull String name
) {}
