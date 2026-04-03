package dnd.manager.app.service;

import org.springframework.stereotype.Service;

import dnd.manager.app.model.CharacterResource;
import dnd.manager.app.repository.CharacterResourceRepository;

@Service
public class CharacterResourceService {

    private final CharacterResourceRepository characterRepository;

    public CharacterResourceService(CharacterResourceRepository characterRepository) {
        this.characterRepository = characterRepository;
    }

    public CharacterResourceRepository getCharacterRepository() {
        return characterRepository;
    }

    public CharacterResource findById(Long id) {
        return characterRepository.findById(id).orElse(null);
    }
    

}
