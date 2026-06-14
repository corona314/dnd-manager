package dnd.manager.app.service.CharacterServices;

import java.util.List;
import org.springframework.stereotype.Service;

import dnd.manager.app.model.CharacterEntities.CharacterAbility;
import dnd.manager.app.repository.CharacterRepositories.CharacterAbilityRepository;

@Service
public class CharacterAbilityService {

    private final CharacterAbilityRepository repository;

    public CharacterAbilityService(CharacterAbilityRepository repository) {
        this.repository = repository;
    }

    public List<CharacterAbility> findAll() {
        return repository.findAll();
    }

    public List<CharacterAbility> findByCharacterId(Long characterId){
        return repository.findByCharacterId(characterId);
    }

    public CharacterAbility save(CharacterAbility characterAbility) {
        return repository.save(characterAbility);
    }

}
