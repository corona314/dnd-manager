package dnd.manager.app.service;

import java.util.List;

import org.springframework.stereotype.Service;
import dnd.manager.app.model.ArmorType;
import dnd.manager.app.repository.ArmorTypeRepository;

@Service
public class ArmorClassService {

    private final ArmorTypeRepository armorTypeRepository;

    public ArmorClassService(ArmorTypeRepository armorTypeRepository) {
        this.armorTypeRepository = armorTypeRepository;
    }

    public List<ArmorType> findAll() {
        return armorTypeRepository.findAll();
    }

    public ArmorType findById(Long id) {
        return armorTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ArmorType not found with id: " + id));
    }
}