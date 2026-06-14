package dnd.manager.app.dto.ClassDto;

import java.util.List;

import dnd.manager.app.dto.SpellDto.SpellSummaryDto;

public record ClassResponseDto (
    String name,
    String hitPointDie,
    List<ClassSkillDto> skills,
    List<ClassSavingThrowDto> savingThrows,
    List<ClassFeatureDto> features,
    List<SpellSummaryDto> spells,
    List<ClassArmorTypeDto> armorTypes
){}
