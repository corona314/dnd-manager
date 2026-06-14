package dnd.manager.app.mapper;

import java.util.Comparator;

import org.springframework.stereotype.Component;

import dnd.manager.app.dto.ClassDto.ClassArmorTypeDto;
import dnd.manager.app.dto.ClassDto.ClassFeatureDto;
import dnd.manager.app.dto.ClassDto.ClassResponseDto;
import dnd.manager.app.dto.ClassDto.ClassSavingThrowDto;
import dnd.manager.app.dto.ClassDto.ClassSkillDto;
import dnd.manager.app.dto.ClassDto.ClassSummaryDto;
import dnd.manager.app.model.ClassEntities.ClassArmorType;
import dnd.manager.app.model.ClassEntities.ClassEntity;
import dnd.manager.app.model.ClassEntities.ClassFeature;
import dnd.manager.app.model.ClassEntities.ClassSavingThrow;
import dnd.manager.app.model.ClassEntities.ClassSkill;
import dnd.manager.app.model.ClassEntities.ClassSpell;

@Component
public class ClassMapper {

    private final FeatureMapper featureMapper;
    private final SpellMapper spellMapper;

    ClassMapper(FeatureMapper featureMapper, SpellMapper spellMapper) {
        this.featureMapper = featureMapper;
        this.spellMapper = spellMapper;
    }

    public ClassSummaryDto toSummaryDto(ClassEntity e) {
        return new ClassSummaryDto(
            e.getId(),
            e.getName(),
            e.getHitPointDie()
        );
    }

    public ClassResponseDto toResponseDto(ClassEntity e) {
        return new ClassResponseDto(
            e.getName(),
            e.getHitPointDie(),
            e.getSkills().stream().map(this::toClassSkillDto).toList(),
            e.getSavingThrows().stream().map(this::toClassSavingThrowDto).toList(),
            e.getFeatures().stream().sorted(Comparator.comparingInt(ClassFeature::getLevel)).map(this::toClassFeatureDto).toList(),
            e.getSpells().stream().sorted(Comparator.comparingInt((ClassSpell cs) -> cs.getSpell().getLevel())).map(cs -> spellMapper.toSummaryDto(cs.getSpell())).toList(),
            e.getArmorTypes().stream().map(this::toClassArmorTypeDto).toList()
        );
    }

    private ClassSkillDto toClassSkillDto(ClassSkill e) {
        return new ClassSkillDto(
            e.getSkill() == null ? null : e.getSkill().getName(),
            e.getSkill() == null ? null : e.getSkill().getAbility() == null ? null : e.getSkill().getAbility().getCode()
        );
    }

    private ClassSavingThrowDto toClassSavingThrowDto(ClassSavingThrow e) {
        return new ClassSavingThrowDto(
            e.getAbility() == null ? null : e.getAbility().getCode()
        );
    }

    private ClassFeatureDto toClassFeatureDto(ClassFeature e) {
        return new ClassFeatureDto(
            featureMapper.toDto(e.getFeature()),
            e.getLevel()
        );
    }

    private ClassArmorTypeDto toClassArmorTypeDto(ClassArmorType e) {
        return new ClassArmorTypeDto(
            e.getArmorType() == null ? null : e.getArmorType().getName()
        );
    }
}
