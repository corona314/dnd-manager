package dnd.manager.app.service;

import java.util.List;

import org.springframework.stereotype.Service;

import dnd.manager.app.model.Armor;
import dnd.manager.app.repository.ArmorRepository;

@Service
public class CharacterStatService {

    private final CharacterStatRepository repository;

    public CharacterStatService(CharacterStatRepository repository) {
        this.repository = repository;
    }

    public List<CharacterStat> findAll() {
        return repository.findAll();
    }

    public CharacterStat findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public List<CharacterStat> findByCharacterId(Long characterId){
        return repository.findByCharacterId(characterId);
    }

    public CharacterStat save(CharacterStat characterStat) {
        return repository.save(characterStat);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
