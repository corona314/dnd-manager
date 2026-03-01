package dnd.manager.app.service;

import java.util.List;

import dnd.manager.app.model.Armor;
import dnd.manager.app.repository.ArmorRepository;

public class ArmorService {

    private final ArmorRepository repository;

    public ArmorService(ArmorRepository repository) {
        this.repository = repository;
    }

    public List<Armor> findAll() {
        return repository.findAll();
    }
}
