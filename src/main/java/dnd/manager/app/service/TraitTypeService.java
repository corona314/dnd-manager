package dnd.manager.app.service;

import java.util.List;

import org.springframework.stereotype.Service;
import dnd.manager.app.model.TraitType;
import dnd.manager.app.repository.TraitTypeRepository;

@Service
class TraitTypeService {

    private final TraitTypeRepository traitTypeRepository;

    TraitTypeService(TraitTypeRepository traitTypeRepository) {
        this.traitTypeRepository = traitTypeRepository;
    }

    public List<TraitType> findAll() {
        return traitTypeRepository.findAll();
    }

    public TraitType findById(Long id) {
        return traitTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("TraitType not found with id: " + id));
    }

    public TraitType findByName(String name) {
        return traitTypeRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("TraitType not found: " + name));
    }

    public TraitType save(TraitType traitType) {
        return traitTypeRepository.save(traitType);
    }
}