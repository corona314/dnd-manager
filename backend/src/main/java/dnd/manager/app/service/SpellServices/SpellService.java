package dnd.manager.app.service.SpellServices;

import dnd.manager.app.dto.SpellDto.SpellResponseDto;
import dnd.manager.app.dto.SpellDto.SpellSummaryDto;
import dnd.manager.app.mapper.SpellMapper;
import dnd.manager.app.model.SpellEntities.Spell;
import dnd.manager.app.repository.SpellRepositories.SpellRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import static dnd.manager.app.repository.SpellRepositories.spec.SpellSpecifications.*;

import java.util.List;

@Service
public class SpellService {

    private final SpellRepository spellRepository;
    private final SpellMapper mapper;

    public SpellService(SpellRepository spellRepository, SpellMapper mapper) {
        this.spellRepository = spellRepository;
        this.mapper = mapper;
    }



    public SpellResponseDto findById(Long id) {
        return mapper.toResponseDto(spellRepository.findById(id).orElse(null));
    }

    public Page<SpellSummaryDto> findSpells(
        String name,
        Integer levelMin,
        Integer levelMax,
        List<Integer> schoolIds,
        String components,
        Boolean concentration,
        Boolean ritual,
        String savingThrowStat,
        Boolean attackRoll,
        List<String> damageTypes,
        int page,
        int size
    ){
        Specification<Spell> spec = Specification
        .where(hasName(name))
        .and(hasLevelBetween(levelMin, levelMax))
        .and(hasSchool(schoolIds))
        .and(hasComponent(components))
        .and(isConcentration(concentration))
        .and(isRitual(ritual))
        .and(hasSavingThrowStat(savingThrowStat))
        .and(isAttackRoll(attackRoll))
        .and(hasDamage(damageTypes));
        
        
        Page<Spell> spells = spellRepository.findAll(
                    spec,
                    PageRequest.of(page, size)
            );
        return spells.map(mapper::toSummaryDto);
    }


}