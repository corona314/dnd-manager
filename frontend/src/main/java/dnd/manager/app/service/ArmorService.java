package dnd.manager.app.service;

import java.util.List;

import org.springframework.stereotype.Service;

import dnd.manager.app.model.Armor;
import dnd.manager.app.repository.ArmorRepository;

@Service
public class ArmorService {

    private final ArmorRepository repository;

    public ArmorService(ArmorRepository repository) {
        this.repository = repository;
    }

    public List<Armor> findAll() {
        return repository.findAll();
    }

    public Armor findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public List<Armor> findByArmorTypeId(Long armorTypeId){
        return repository.findByArmorTypeId(armorTypeId);
    }

    public Armor save(Armor armor) {
        return repository.save(armor);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
