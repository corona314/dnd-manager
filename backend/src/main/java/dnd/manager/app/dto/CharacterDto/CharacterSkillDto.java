package dnd.manager.app.dto.CharacterDto;

import jakarta.validation.constraints.NotNull;

public record CharacterSkillDto(
    @NotNull Long skillId,
    Boolean proficient,
    Boolean expertise
) {}