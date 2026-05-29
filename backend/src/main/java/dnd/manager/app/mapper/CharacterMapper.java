package dnd.manager.app.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import dnd.manager.app.dto.CharacterDto.CharacterCreateDto;
import dnd.manager.app.dto.CharacterDto.CharacterItemDto;
import dnd.manager.app.dto.CharacterDto.CharacterResponseDto;
import dnd.manager.app.dto.CharacterDto.CharacterSkillDto;
import dnd.manager.app.dto.CharacterDto.CharacterStatDto;
import dnd.manager.app.dto.CharacterDto.CharacterSummaryDto;
import dnd.manager.app.model.CharacterEntities.CharacterEntity;
import dnd.manager.app.model.CharacterEntities.CharacterItem;
import dnd.manager.app.model.CharacterEntities.CharacterSkill;
import dnd.manager.app.model.CharacterEntities.CharacterStat;
import dnd.manager.app.model.CharacterEntities.CharacterStatus;

@Component
public class CharacterMapper {

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
            e.getId(),
            e.getName(),
            e.getLevel(),
            e.getMaxHp(),
            e.getCurrentHp(),
            e.getWalkSpeed(),
            e.getFlySpeed(),
            e.getSpecies() != null ? e.getSpecies().getId() : null,
            e.getClassEntity() != null ? e.getClassEntity().getId() : null,
            e.getSubclass() != null ? e.getSubclass().getId() : null,
            e.getBackground() != null ? e.getBackground().getId() : null,
            e.getStatus(),
            e.getCreatedAt(),
            e.getUpdatedAt(),
            e.getFinalizedAt(),
            e.getStats() != null ? e.getStats().stream().map(this::toStatDto).toList() : List.of(),
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

    private CharacterStatDto toStatDto(CharacterStat stat) {
        return new CharacterStatDto(
            stat.getStat().getId(),
            stat.getBaseValue()
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