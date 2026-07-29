package dnd.manager.app.service;

import dnd.manager.app.dto.SkillDto;
import dnd.manager.app.mapper.SkillMapper;
import dnd.manager.app.model.Skill;
import dnd.manager.app.repository.SkillRepositories.SkillRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import static dnd.manager.app.repository.SkillRepositories.spec.SkillSpecifications.*;

@Service
public class SkillService {

    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;

    public SkillService(SkillRepository skillRepository, SkillMapper skillMapper) {
        this.skillRepository = skillRepository;
        this.skillMapper = skillMapper;
    }

    public List<Skill> findAll() {
        return skillRepository.findAll();
    }

    public SkillDto findById(Long id) {
        return skillRepository.findById(id).map(skillMapper::toDto).orElse(null);
    }

    public Page<SkillDto> findSkills(
        String name,
        String ability,
        Pageable pageable
    ){
        Specification<Skill> spec = Specification
        .where(hasName(name))
        .and(hasAbility(ability));        
        
        Page<Skill> skills = skillRepository.findAll(spec, pageable);
        
        return skills.map(skillMapper::toDto);
    }

}