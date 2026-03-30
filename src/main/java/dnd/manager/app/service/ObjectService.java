package dnd.manager.app.service;

import dnd.manager.app.model.ObjectEntity;
import dnd.manager.app.repository.ObjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ObjectService {

    private final ObjectRepository objectRepository;

    public ObjectService(ObjectRepository objectRepository) {
        this.objectRepository = objectRepository;
    }

    public List<ObjectEntity> findAll() {
        return objectRepository.findAll();
    }

    public Optional<ObjectEntity> findById(Long id) {
        return objectRepository.findById(id);
    }

    public ObjectEntity save(ObjectEntity object) {
        return objectRepository.save(object);
    }

    public void deleteById(Long id) {
        objectRepository.deleteById(id);
    }
}