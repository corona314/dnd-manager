package dnd.manager.app.service;

import java.util.List;

import org.springframework.stereotype.Service;
import dnd.manager.app.model.ObjectType;
import dnd.manager.app.repository.ObjectTypeRepository;


@Service
public class ObjectTypeService {

    private final ObjectTypeRepository objectTypeRepository;

    ObjectTypeService(ObjectTypeRepository objectTypeRepository) {
        this.objectTypeRepository = objectTypeRepository;
    }

    public List<ObjectType> findAll() {
        return objectTypeRepository.findAll();
    }

    public ObjectType findById(Long id) {
        return objectTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ObjectType not found with id: " + id));
    }

    public ObjectType save(ObjectType objectType) {
        return objectTypeRepository.save(objectType);
    }
}
