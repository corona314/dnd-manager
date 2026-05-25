package dnd.manager.app.dto.CharacterDto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CharacterStatDto(
    @NotNull Long statId,
    @NotNull @Min(1) @Max(30) Integer baseValue
) {}