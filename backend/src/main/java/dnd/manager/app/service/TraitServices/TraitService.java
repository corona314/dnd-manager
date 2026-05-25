package dnd.manager.app.service.TraitServices;

import java.util.List;

import org.springframework.stereotype.Service;

import dnd.manager.app.model.TraitEntities.Trait;
import dnd.manager.app.repository.TraitRepositories.TraitRepository;

@Service
public class TraitService {

    private final TraitRepository traitRepository;

    public TraitService(TraitRepository traitRepository) {
        this.traitRepository = traitRepository;
    }

    public List<Trait> findAll() {
        return traitRepository.findAll();
    }

    public Trait findById(Long id) {
        return traitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trait not found with id: " + id));
    }

    public Trait save(Trait trait) {
        return traitRepository.save(trait);
    }

    public void deleteById(Long id) {
        traitRepository.deleteById(id);
    }

    // Todos los traits de un tipo concreto (racial, class, feat...)
    public List<Trait> findByType(Long traitTypeId) {
        return traitRepository.findByTraitTypeId(traitTypeId);
    }
}