package dnd.manager.app.dto.ClassDto;

import java.util.List;

public record ClassResponseDto (
    Long id,
    String name,
    String hitPointDie,
    List<ClassSkillDto> skills,
    List<ClassSavingThrow> savingThrows,
    List<ClassFeatureDto> features,
    List<ClassArmorTypeDto> armorTypes
){}
