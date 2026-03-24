package dnd.manager.app.service;

import dnd.manager.app.model.CharacterResource;
import dnd.manager.app.repository.CharacterResourceRepository;

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
