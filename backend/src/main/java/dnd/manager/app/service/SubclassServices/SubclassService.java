package dnd.manager.app.service.SubclassServices;

import java.util.List;

import org.springframework.stereotype.Service;

import dnd.manager.app.model.SubclassEntities.Subclass;
import dnd.manager.app.repository.SubclassRepositories.SubclassRepository;
import dnd.manager.app.repository.SubclassRepositories.SubclassTraitRepository;

@Service
public class SubclassService {

    private final SubclassRepository subclassRepository;
    private final SubclassTraitRepository subclassTraitRepository;

    public SubclassService(SubclassRepository subclassRepository,
                           SubclassTraitRepository subclassTraitRepository) {
        this.subclassRepository = subclassRepository;
        this.subclassTraitRepository = subclassTraitRepository;
    }

    public List<Subclass> findAll() {
        return subclassRepository.findAll();
    }

    public Subclass findById(Long id) {
        return subclassRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subclass not found with id: " + id));
    }

    // Subclases de una clase concreta — útil para el select del front
    public List<Subclass> findByClass(Long classId) {
        return subclassRepository.findByClassEntityId(classId);
    }

    public Subclass save(Subclass subclass) {
        return subclassRepository.save(subclass);
    }

    public void deleteById(Long id) {
        subclassRepository.deleteById(id);
    }

    // Traits de subclase filtrados por nivel del personaje
    public List<?> findTraitsBySubclassAndLevel(Long subclassId, Integer level) {
        return subclassTraitRepository.findBySubclassIdAndLevelLessThanEqual(subclassId, level);
    }
}