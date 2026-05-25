package dnd.manager.app.dto.CharacterDto;

import jakarta.validation.constraints.Min;

public record CharacterCreateDto(
    String name,
    @Min(1) Integer level
) {}
