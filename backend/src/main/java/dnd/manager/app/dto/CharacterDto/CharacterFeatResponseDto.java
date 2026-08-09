package dnd.manager.app.dto.CharacterDto;

import dnd.manager.app.dto.FeatDto;

public record CharacterFeatResponseDto(
    FeatDto feat,
    String source,
    Integer sourceLevel
) {}