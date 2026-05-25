package dnd.manager.app.DTO;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record CharacterCreateDto(
    String name,
    @Min(1) @Max(20) Integer level
) {}
