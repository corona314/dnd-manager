package dnd.manager.app.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import dnd.manager.app.dto.SpellDto.SpellDamageTypeDto;
import dnd.manager.app.dto.SpellDto.SpellResponseDto;
import dnd.manager.app.dto.SpellDto.SpellSummaryDto;
import dnd.manager.app.model.SpellEntities.Spell;

@Component
public class SpellMapper {


    public SpellSummaryDto toSummaryDto(Spell s){
        return new SpellSummaryDto(
            s.getId(),
            s.getName(),
            s.getLevel(),
            s.getSchool().getName(),
            s.getComponents(),
            s.getConcentration(),
            s.getRitual(),
            s.getSavingThrowStat() != null ? s.getSavingThrowStat().getCode() : null,
            s.getAttackRoll(),
            toSpellDamageTypeDto(s)
        );
    }

    public SpellResponseDto toResponseDto(Spell s){
        return new SpellResponseDto(
            s.getName(),
            s.getLevel(),
            s.getSchool().getName(),
            s.getCastingTime(),
            s.getRange(),
            s.getDuration(),
            s.getComponents(),
            s.getMaterial(),
            s.getConcentration(),
            s.getRitual(),
            s.getDescription(),
            s.getSavingThrowStat() != null ? s.getSavingThrowStat().getCode() : null,
            s.getAttackRoll(),
            s.getDamageRoll(),
            toSpellDamageTypeDto(s)
        );
        
    }

    private List<SpellDamageTypeDto> toSpellDamageTypeDto(Spell s) {
        if (s.getDamageTypes() == null) return List.of();
        return s.getDamageTypes().stream()
            .map(dt -> new SpellDamageTypeDto(dt.getDamageType().getName(), dt.getAlways()))
            .toList();
    }
}
