package dnd.manager.app.service;

import java.util.List;

import org.springframework.stereotype.Service;

import dnd.manager.app.model.ArmorType;
import dnd.manager.app.repository.ArmorTypeRepository;

@Service
public class ArmorTypeService {
    private final ArmorTypeRepository armorTypeRepository;

    public ArmorTypeService(ArmorTypeRepository armorTypeRepository) {
        this.armorTypeRepository = armorTypeRepository;
    }

    public List<ArmorType> findAll() {
        return armorTypeRepository.findAll();
    }

    public ArmorType findById(Long id) {
        return armorTypeRepository.findById(id).orElse(null);
    }
}
