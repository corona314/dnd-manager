package dnd.manager.app.service.ItemServices;

import java.util.List;

import org.springframework.stereotype.Service;

import dnd.manager.app.model.ItemEntities.ItemType;
import dnd.manager.app.repository.ItemRepositories.ItemTypeRepository;


@Service
public class ItemTypeService {

    private final ItemTypeRepository objectTypeRepository;

    ItemTypeService(ItemTypeRepository objectTypeRepository) {
        this.objectTypeRepository = objectTypeRepository;
    }

    public List<ItemType> findAll() {
        return objectTypeRepository.findAll();
    }

    public ItemType findById(Long id) {
        return objectTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ObjectType not found with id: " + id));
    }

    public ItemType save(ItemType objectType) {
        return objectTypeRepository.save(objectType);
    }
}
