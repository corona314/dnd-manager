package dnd.manager.app.mapper;

import java.util.Collections;
import java.util.List;

import dnd.manager.app.dto.CharacterDto.CharacterResponseDto;
import dnd.manager.app.dto.CharacterDto.CharacterSkillDto;
import dnd.manager.app.dto.CharacterDto.CharacterStatDto;
import dnd.manager.app.dto.CharacterDto.CharacterSummaryDto;
import dnd.manager.app.model.CharacterEntities.CharacterEntity;
import dnd.manager.app.model.CharacterEntities.CharacterSkill;
import dnd.manager.app.model.CharacterEntities.CharacterStat;

public class CharacterMapper {

    public static CharacterSummaryDto toSummaryDto(CharacterEntity e) {
        return new CharacterSummaryDto(
            e.getId(),
            e.getName(),
            e.getLevel(),
            e.getStatus(),
            e.getUpdatedAt()
        );
    }

    public static CharacterResponseDto toResponseDto(CharacterEntity e) {
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
            mapStats(e.getStats()),
            mapSkills(e.getSkills())
        );
    }

    private static List<CharacterStatDto> mapStats(List<CharacterStat> stats) {
        if (stats == null) return Collections.emptyList();
        return stats.stream()
            .map(s -> new CharacterStatDto(s.getStat().getId(), s.getBaseValue()))
            .toList();
    }

    private static List<CharacterSkillDto> mapSkills(List<CharacterSkill> skills) {
        if (skills == null) return Collections.emptyList();
        return skills.stream()
            .map(s -> new CharacterSkillDto(s.getSkill().getId(), s.getProficient(), s.getExpertise()))
            .toList();
    }
}