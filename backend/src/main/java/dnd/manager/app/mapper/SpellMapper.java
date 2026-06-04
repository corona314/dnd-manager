package dnd.manager.app.mapper;

import org.springframework.stereotype.Component;

import dnd.manager.app.dto.SpellDto.SpellResponseDto;
import dnd.manager.app.dto.SpellDto.SpellSummaryDto;
import dnd.manager.app.model.SpellEntities.Spell;

@Component
public class SpellMapper {


    public SpellSummaryDto toSummaryDto(Spell s){
        return new SpellSummaryDto(
            s.getName(),
            s.getLevel(),
            s.getSchool().getName(),
            s.getComponents(),
            s.getConcentration(),
            s.getRitual(),
            s.getSavingThrowStat() != null ? s.getSavingThrowStat().getCode() : null,
            s.getAttackRoll(),
            s.getDamageType() != null ? s.getDamageType().getName() : null
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
            s.getDamageType() != null ? s.getDamageType().getName() : null
        );
        
    }
}
