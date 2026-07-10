package dnd.manager.app.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import dnd.manager.app.dto.CharacterDto.CharacterCreateDto;
import dnd.manager.app.dto.CharacterDto.CharacterItemDto;
import dnd.manager.app.dto.CharacterDto.CharacterResponseDto;
import dnd.manager.app.dto.CharacterDto.CharacterSkillDto;
import dnd.manager.app.dto.CharacterDto.CharacterAbilityDto;
import dnd.manager.app.dto.CharacterDto.CharacterSummaryDto;
import dnd.manager.app.model.CharacterEntities.CharacterEntity;
import dnd.manager.app.model.CharacterEntities.CharacterItem;
import dnd.manager.app.model.CharacterEntities.CharacterSkill;
import dnd.manager.app.model.CharacterEntities.CharacterStatus;
import dnd.manager.app.model.CharacterEntities.CharacterAbility;

@Component
public class CharacterMapper {

    private final SpeciesMapper speciesMapper;
    private final ClassMapper classMapper;
    private final BackgroundMapper backgroundMapper;

    public CharacterMapper(SpeciesMapper speciesMapper, ClassMapper classMapper, BackgroundMapper backgroundMapper) {
        this.speciesMapper = speciesMapper;
        this.classMapper = classMapper;
        this.backgroundMapper = backgroundMapper;
    }

    public CharacterSummaryDto toSummaryDto(CharacterEntity e) {
        return new CharacterSummaryDto(
            e.getId(),
            e.getName(),
            e.getLevel(),
            e.getStatus(),
            e.getUpdatedAt()
        );
    }

    public CharacterResponseDto toResponseDto(CharacterEntity e) {
        return new CharacterResponseDto(
            e.getName(),
            e.getLevel(),
            e.getMaxHp(),
            e.getCurrentHp(),
            e.getWalkSpeed(),
            e.getFlySpeed(),
            speciesMapper.toSummaryDto(e.getSpecies()),
            classMapper.toSummaryDto(e.getClassEntity()),
            classMapper.subclassToSummaryDto(e.getSubclass()),
            backgroundMapper.toSummaryDto(e.getBackground()),
            e.getStatus(),
            e.getCreatedAt(),
            e.getUpdatedAt(),
            e.getFinalizedAt(),
            e.getAbilities() != null ? e.getAbilities().stream().map(this::toStatDto).toList() : List.of(),
            e.getSkills() != null ? e.getSkills().stream().map(this::toSkillDto).toList() : List.of(),
            e.getItems() != null ? e.getItems().stream().map(this::toItemDto).toList() : List.of()
        );
    }

    public CharacterEntity toEntity(CharacterCreateDto dto) {
    CharacterEntity entity = new CharacterEntity();
    entity.setName(dto.name());
    entity.setStatus(CharacterStatus.DRAFT);
    return entity;

    }

    private CharacterAbilityDto toStatDto(CharacterAbility ability) {
        return new CharacterAbilityDto(
            ability.getAbility().getCode(),
            ability.getBaseValue()
        );
    }

    private CharacterSkillDto toSkillDto(CharacterSkill skill) {
        return new CharacterSkillDto(
            skill.getSkill().getId(),
            skill.getProficient(),
            skill.getExpertise()
        );
    }

    private CharacterItemDto toItemDto(CharacterItem item){
        return new CharacterItemDto(
            item.getQuantity(), 
            item.getEquipped(), 
            item.getAttuned()
        );
    }
}