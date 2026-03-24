package dnd.manager.app.service;

import java.util.List;

import org.springframework.stereotype.Service;

import dnd.manager.app.model.Skill;
import dnd.manager.app.repository.SkillRepository;

@Service
class SkillService {

    private final SkillRepository skillRepository;

    SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    public List<Skill> findAll() {
        return skillRepository.findAll();
    }

    public Skill findById(Long id) {
        return skillRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Skill not found with id: " + id));
    }

    // Skills agrupadas por stat — útil para renderizar la ficha
    public List<Skill> findByStatId(Long statId) {
        return skillRepository.findByStatId(statId);
    }
}