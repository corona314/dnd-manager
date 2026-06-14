package dnd.manager.app.dto.ClassDto;

import java.util.List;

public record ClassResponseDto (
    String name,
    String hitPointDie,
    List<ClassSkillDto> skills,
    List<ClassSavingThrowDto> savingThrows,
    List<ClassFeatureDto> features,
    List<ClassArmorTypeDto> armorTypes
){}
