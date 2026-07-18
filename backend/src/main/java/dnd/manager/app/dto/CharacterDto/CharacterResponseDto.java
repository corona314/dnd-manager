package dnd.manager.app.dto.CharacterDto;

import java.time.LocalDateTime;
import java.util.List;

import dnd.manager.app.dto.BackgroundDto.BackgroundSummaryDto;
import dnd.manager.app.dto.ClassDto.ClassSummaryDto;
import dnd.manager.app.dto.ClassDto.SubclassSummaryDto;
import dnd.manager.app.dto.SpeciesDto.SpeciesSummaryDto;
import dnd.manager.app.model.CharacterEntities.CharacterStatus;

/*
    Clase con toda la información del personaje
*/

public record CharacterResponseDto(
    String name,
    Integer level,
    Integer maxHp,
    Integer currentHp,
    Integer walkSpeed,
    Integer flySpeed,
    SpeciesSummaryDto species,
    ClassSummaryDto classEntity,
    SubclassSummaryDto subclass,
    BackgroundSummaryDto background,
    List<CharacterAbilityDto> abilities,
    List<CharacterSkillResponseDto> skills,
    List<CharacterItemResponseDto> items,
    CharacterStatus status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    LocalDateTime finalizedAt

) {}