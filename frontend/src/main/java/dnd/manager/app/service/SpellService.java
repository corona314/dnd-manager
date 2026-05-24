package dnd.manager.app.service;

import dnd.manager.app.model.Spell;
import dnd.manager.app.repository.SpellRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SpellService {

    private final SpellRepository spellRepository;

    public SpellService(SpellRepository spellRepository) {
        this.spellRepository = spellRepository;
    }

    public List<Spell> findAll() {
        return spellRepository.findAll();
    }

    public Optional<Spell> findById(Long id) {
        return spellRepository.findById(id);
    }

    public Optional<Spell> findByName(String name) {
        return spellRepository.findByName(name);
    }

    public List<Spell> findByLevel(Integer level) {
        return spellRepository.findByLevel(level);
    }

    public List<Spell> findBySchoolId(Long schoolId) {
        return spellRepository.findBySchoolId(schoolId);
    }

    public List<Spell> findByConcentration(Boolean concentration) {
        return spellRepository.findByConcentration(concentration);
    }

    public List<Spell> findByRitual(Boolean ritual) {
        return spellRepository.findByRitual(ritual);
    }

    public Spell save(Spell spell) {
        return spellRepository.save(spell);
    }

    public void deleteById(Long id) {
        spellRepository.deleteById(id);
    }
}