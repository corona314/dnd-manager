package dnd.manager.app.service;

import java.util.List;
import org.springframework.stereotype.Service;
import dnd.manager.app.model.SpellSchool;
import dnd.manager.app.repository.SpellSchoolRepository;
import dnd.manager.app.repository.SpellRepository;

// ---------------------------------------------------------------
// SpellSchoolService.java
// ---------------------------------------------------------------
@Service
public class SpellSchoolService {

    private final SpellSchoolRepository spellSchoolRepository;
    private final SpellRepository spellRepository;

    public SpellSchoolService(SpellSchoolRepository spellSchoolRepository,
                              SpellRepository spellRepository) {
        this.spellSchoolRepository = spellSchoolRepository;
        this.spellRepository = spellRepository;
    }

    public List<SpellSchool> findAll() {
        return spellSchoolRepository.findAll();
    }

    public SpellSchool findById(Long id) {
        return spellSchoolRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("SpellSchool not found with id: " + id));
    }

    // Todos los hechizos de una escuela concreta
    public List<?> findSpellsBySchool(Long schoolId) {
        return spellRepository.findBySchoolId(schoolId);
    }
}