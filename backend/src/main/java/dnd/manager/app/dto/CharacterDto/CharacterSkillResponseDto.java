package dnd.manager.app.dto.CharacterDto;

import dnd.manager.app.dto.SkillDto;

public record CharacterSkillResponseDto(
    SkillDto skill,
    Boolean proficient,
    Boolean expertise
) {}