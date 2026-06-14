package dnd.manager.app.service;

import dnd.manager.app.model.Ability;
import dnd.manager.app.repository.AbilityRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AbilityService {

    private final AbilityRepository abilityRepository;

    public AbilityService(AbilityRepository abilityRepository) {
        this.abilityRepository = abilityRepository;
    }

    public List<Ability> findAll() {
        return abilityRepository.findAll();
    }

    public Optional<Ability> findById(Long id) {
        return abilityRepository.findById(id);
    }

    public Optional<Ability> findByCode(String code) {
        return abilityRepository.findByCode(code);
    }

    public Ability save(Ability ability) {
        return abilityRepository.save(ability);
    }

    public void deleteById(Long id) {
        abilityRepository.deleteById(id);
    }
}