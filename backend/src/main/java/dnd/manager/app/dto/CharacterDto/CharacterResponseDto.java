package dnd.manager.app.dto.CharacterDto;

import java.time.LocalDateTime;
import java.util.List;

import dnd.manager.app.model.CharacterEntities.CharacterAbilityus;

/*
    Clase con toda la información del personaje
*/

public record CharacterResponseDto(
    Long id,
    String name,
    Integer level,
    Integer maxHp,
    Integer currentHp,
    Integer walkSpeed,
    Integer flySpeed,
    Long speciesId,
    Long classId,
    Long subclassId,
    Long backgroundId,
    CharacterAbilityus status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    LocalDateTime finalizedAt,
    List<CharacterAbilityDto> abilities,
    List<CharacterSkillDto> skills,
    List<CharacterItemDto> items

) {}