package dnd.manager.app.dto.CharacterDto;

import dnd.manager.app.dto.SpellDto.SpellSummaryDto;

public record CharacterSpellResponseDto(
    SpellSummaryDto spell,
    Boolean prepared,
    Boolean alwaysPrepared
) {}
