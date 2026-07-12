package dnd.manager.app.mapper;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Component;

import dnd.manager.app.dto.ClassDto.ClassArmorTypeDto;
import dnd.manager.app.dto.ClassDto.ClassFeatureDto;
import dnd.manager.app.dto.ClassDto.ClassResponseDto;
import dnd.manager.app.dto.ClassDto.ClassSavingThrowDto;
import dnd.manager.app.dto.ClassDto.ClassSummaryDto;
import dnd.manager.app.dto.ClassDto.SubclassFeatureDto;
import dnd.manager.app.dto.ClassDto.SubclassResponseDto;
import dnd.manager.app.dto.ClassDto.SubclassSpellDto;
import dnd.manager.app.dto.ClassDto.SubclassSummaryDto;
import dnd.manager.app.model.ClassEntities.ClassArmorType;
import dnd.manager.app.model.ClassEntities.ClassEntity;
import dnd.manager.app.model.ClassEntities.ClassFeature;
import dnd.manager.app.model.ClassEntities.ClassSavingThrow;
import dnd.manager.app.model.ClassEntities.ClassSkill;
import dnd.manager.app.model.ClassEntities.ClassSpell;
import dnd.manager.app.model.SubclassEntities.Subclass;
import dnd.manager.app.repository.SubclassRepositories.SubclassRepository;

@Component
public class ClassMapper {

    private final FeatureMapper featureMapper;
    private final SpellMapper spellMapper;
    private final SkillMapper skillMapper;
    private final SubclassRepository subclassRepository;


    ClassMapper(FeatureMapper featureMapper, SpellMapper spellMapper, dnd.manager.app.repository.SubclassRepositories.SubclassRepository subclassRepository, SkillMapper skillMapper) {
        this.featureMapper = featureMapper;
        this.spellMapper = spellMapper;
        this.skillMapper = skillMapper;
        this.subclassRepository = subclassRepository;
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
            e.getNumberSkills(),
            e.getSkills().stream().map(ClassSkill::getSkill).map(skillMapper::toDto).toList(),
            e.getSavingThrows().stream().map(this::toClassSavingThrowDto).toList(),
            e.getFeatures().stream().sorted(Comparator.comparingInt(ClassFeature::getLevel)).map(this::toClassFeatureDto).toList(),
            e.getSpells().stream().sorted(Comparator.comparingInt((ClassSpell cs) -> cs.getSpell().getLevel())).map(cs -> spellMapper.toSummaryDto(cs.getSpell())).toList(),
            e.getArmorTypes().stream().map(this::toClassArmorTypeDto).toList(),
            e.getShield(),
            subclassRepository.findByClassEntityId(e.getId()).stream().map(this::subclassToSummaryDto).toList()
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

    public SubclassSummaryDto subclassToSummaryDto(Subclass e) {
        return new SubclassSummaryDto(
            e.getId(),
            e.getName()
        );
    }

    public SubclassResponseDto subclassToResponseDto(Subclass e) {
        return new SubclassResponseDto(
            e.getName(),
            e.getSubclassFeatures() == null ? List.of() : e.getSubclassFeatures().stream()
                .map(sf -> new SubclassFeatureDto(
                    featureMapper.toDto(sf.getFeature()),
                    sf.getLevel()
                ))
                .sorted(Comparator.comparingInt(SubclassFeatureDto::level))
                .toList(),
            e.getSpells() == null ? List.of() : e.getSpells().stream()
                .map(ss -> new SubclassSpellDto(
                    spellMapper.toSummaryDto(ss.getSpell())
                ))
                .toList()
        );
    }
}
