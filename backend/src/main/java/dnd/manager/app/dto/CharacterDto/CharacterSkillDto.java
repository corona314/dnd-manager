package dnd.manager.app.dto.CharacterDto;

public record CharacterSkillDto(
    Long skillId,
    Boolean proficient,
    Boolean expertise
) {}