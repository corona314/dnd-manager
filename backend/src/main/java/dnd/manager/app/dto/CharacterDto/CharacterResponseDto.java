package dnd.manager.app.dto.CharacterDto;

import java.time.LocalDateTime;
import java.util.List;

import dnd.manager.app.dto.FeatureDto;
import dnd.manager.app.dto.BackgroundDto.BackgroundSummaryDto;
import dnd.manager.app.dto.SpeciesDto.SpeciesSummaryDto;
import dnd.manager.app.model.CharacterEntities.CharacterStatus;

/*
    Clase con toda la información del personaje
*/

public record CharacterResponseDto(
    String name,
    Integer level,
    Integer currentHp,
    Integer maxHp,
    Integer money,
    Integer experience,
    Integer walkSpeed,
    Integer flySpeed,
    SpeciesSummaryDto species,
    BackgroundSummaryDto background,
    List<CharacterClassResponseDto> classes,
    List<CharacterAbilityDto> abilities,
    List<CharacterSkillResponseDto> skills,
    List<FeatureDto> features,
    List<CharacterItemResponseDto> items,
    List<CharacterSpellResponseDto> spells,
    List<CharacterFeatResponseDto> feats,
    List<CharacterResourceDto> resources,
    CharacterStatus status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    LocalDateTime finalizedAt

) {}