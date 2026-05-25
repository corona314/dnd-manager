package dnd.manager.app.service.CharacterServices;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import dnd.manager.app.model.CharacterEntities.CharacterEntity;
import dnd.manager.app.repository.CharacterRepositories.CharacterRepository;

@Service
public class CharacterService {

    private final CharacterRepository repository;

    public CharacterService(CharacterRepository repository) {
        this.repository = repository;
    }

    public List<CharacterEntity> findByUserId(Long userId) {
        return repository.findByUserId(userId);
    }

    public Optional<CharacterEntity> findById(Long id) {
        return repository.findById(id);
    }
}