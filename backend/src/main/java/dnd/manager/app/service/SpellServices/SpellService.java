package dnd.manager.app.service.SpellServices;

import dnd.manager.app.dto.SpellDto.SpellSummaryDto;
import dnd.manager.app.mapper.SpellMapper;
import dnd.manager.app.model.SpellEntities.Spell;
import dnd.manager.app.repository.SpellRepositories.SpellRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import static dnd.manager.app.repository.SpellRepositories.spec.SpellSpecifications.*;

@Service
public class SpellService {

    private final SpellRepository spellRepository;
    private final SpellMapper mapper;

    public SpellService(SpellRepository spellRepository, SpellMapper mapper) {
        this.spellRepository = spellRepository;
        this.mapper = mapper;
    }

    public List<Spell> findAll() {
        return spellRepository.findAll();
    }

    public Optional<Spell> findById(Long id) {
        return spellRepository.findById(id);
    }

    public Spell save(Spell spell) {
        return spellRepository.save(spell);
    }

    public void deleteById(Long id) {
        spellRepository.deleteById(id);
    }

    public Page<SpellSummaryDto> findSpells(
        String name,
        Integer level,
        Integer schoolId,
        String components,
        Integer concentration,
        Integer ritual,
        String savingThrowStat,
        Boolean attackRoll,
        String damageType,
        int page,
        int size
    ){
        Specification<Spell> spec = Specification
        .where(hasName(name))
        .and(hasLevel(level))
        .and(hasSchool(schoolId))
        .and(hasComponent(components))
        .and(isConcentration(concentration == null ? null : concentration == 1))
        .and(isRitual(ritual == null ? null : ritual == 1))
        .and(hasSavingThrowStat(savingThrowStat))
        .and(isAttackRoll(attackRoll))
        .and(hasDamage(damageType));
        
        
        Page<Spell> spells = spellRepository.findAll(
                    spec,
                    PageRequest.of(page, size)
            );
        return spells.map(mapper::toSummaryDto);
    }


}