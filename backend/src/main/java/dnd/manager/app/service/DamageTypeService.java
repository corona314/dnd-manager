package dnd.manager.app.service;

import java.util.List;

import org.springframework.stereotype.Service;
import dnd.manager.app.model.DamageType;
import dnd.manager.app.repository.DamageTypeRepository;


@Service
class DamageTypeService {

    private final DamageTypeRepository damageTypeRepository;

    DamageTypeService(DamageTypeRepository damageTypeRepository) {
        this.damageTypeRepository = damageTypeRepository;
    }

    public List<DamageType> findAll() {
        return damageTypeRepository.findAll();
    }

    public DamageType findById(Long id) {
        return damageTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("DamageType not found with id: " + id));
    }

    public DamageType save(DamageType damageType) {
        return damageTypeRepository.save(damageType);
    }
}