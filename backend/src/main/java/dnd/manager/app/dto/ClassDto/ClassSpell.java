package dnd.manager.app.dto.ClassDto;

import java.util.List;

import dnd.manager.app.dto.SpellDto.SpellSummaryDto;

public record ClassSpell (
    List<SpellSummaryDto> spells
){}
