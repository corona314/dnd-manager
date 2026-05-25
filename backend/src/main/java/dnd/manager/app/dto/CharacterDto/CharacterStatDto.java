package dnd.manager.app.dto.CharacterDto;

import jakarta.validation.constraints.NotNull;


public record CharacterStatDto(
    @NotNull Long statId,
    @NotNull Integer baseValue
) {}