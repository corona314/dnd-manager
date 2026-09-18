package dnd.manager.app.service.ClassServices;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import dnd.manager.app.dto.ClassDto.ClassResponseDto;
import dnd.manager.app.dto.ClassDto.ClassSummaryDto;
import dnd.manager.app.dto.ClassDto.SubclassResponseDto;
import dnd.manager.app.dto.ClassDto.SubclassSummaryDto;
import dnd.manager.app.dto.SpellDto.SpellSummaryDto;
import dnd.manager.app.mapper.ClassMapper;
import dnd.manager.app.mapper.SpellMapper;
import dnd.manager.app.model.ClassEntities.ClassEntity;
import dnd.manager.app.model.SpellEntities.Spell;
import dnd.manager.app.repository.ClassRepositories.ClassRepository;
import dnd.manager.app.repository.SpellRepositories.SpellRepository;
import dnd.manager.app.service.SubclassServices.SubclassService;
import static dnd.manager.app.repository.ClassRepositories.spec.ClassSpecifications.*;
import static dnd.manager.app.repository.SpellRepositories.spec.SpellSpecifications.*;


import java.util.List;

@Service
public class ClassService {

    private final ClassRepository classRepository;
    private final ClassMapper mapper;
    private final SubclassService subclassService;
    private final SpellRepository spellRepository;
    private final SpellMapper spellMapper;

    public ClassService(ClassRepository classRepository, ClassMapper mapper, SubclassService subclassService, SpellMapper spellMapper, SpellRepository spellRepository) {
        this.classRepository = classRepository;
        this.mapper = mapper;
        this.subclassService = subclassService;
        this.spellRepository = spellRepository;
        this.spellMapper = spellMapper;
    }

    public ClassResponseDto findById(Long id) {
        return mapper.toResponseDto(classRepository.findById(id).orElse(null));
    }

    public Page<ClassSummaryDto> findClasses(
        String name,
        List<String> hitPointDie,
        Pageable pageable
    ){
        Specification<ClassEntity> spec = Specification
        .where(hasClassName(name))
        .and(hasHitPointDie(hitPointDie));        
        
        Page<ClassEntity> classes = classRepository.findAll(spec, pageable);
        
        return classes.map(mapper::toSummaryDto);
    }

    public List<SubclassSummaryDto> findSubclassesByClass(Long classId) {
        return subclassService.findByClassDto(classId);
    }

    public SubclassResponseDto findSubclassById(Long subclassId) {
        return subclassService.findByIdDto(subclassId);
    }

    public Page<SpellSummaryDto> findSpells(
        String name,
        Integer levelMin,
        Integer levelMax,
        List<Integer> schoolIds,
        String components,
        Boolean concentration,
        Boolean ritual,
        String savingThrowAbility,
        Boolean attackRoll,
        List<String> damageTypes,
        Pageable pageable
    ){
        Specification<Spell> spec = Specification
        .where(hasName(name))
        .and(hasLevelBetween(levelMin, levelMax))
        .and(hasSchool(schoolIds))
        .and(hasComponent(components))
        .and(isConcentration(concentration))
        .and(isRitual(ritual))
        .and(hasSavingThrowAbility(savingThrowAbility))
        .and(isAttackRoll(attackRoll))
        .and(hasDamage(damageTypes));
        
        Page<Spell> spells = spellRepository.findAll(spec, pageable);
        
        return spells.map(spellMapper::toSummaryDto);
    }

}