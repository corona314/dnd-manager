package dnd.manager.app.service.ItemServices;

import dnd.manager.app.model.ItemEntities.Item;
import dnd.manager.app.repository.ItemRepositories.ItemRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ItemService {

    private final ItemRepository objectRepository;

    public ItemService(ItemRepository objectRepository) {
        this.objectRepository = objectRepository;
    }

    public List<Item> findAll() {
        return objectRepository.findAll();
    }

    public Optional<Item> findById(Long id) {
        return objectRepository.findById(id);
    }

    public Item save(Item item) {
        return objectRepository.save(item);
    }

    public void deleteById(Long id) {
        objectRepository.deleteById(id);
    }
}