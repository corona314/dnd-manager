package dnd.manager.app.service.CharacterServices;

import org.springframework.stereotype.Service;

import dnd.manager.app.repository.CharacterRepositories.CharacterResourceRepository;

@Service
public class CharacterResourceService {

    private final CharacterResourceRepository characterRepository;

    public CharacterResourceService(CharacterResourceRepository characterRepository) {
        this.characterRepository = characterRepository;
    }

    public CharacterResourceRepository getCharacterRepository() {
        return characterRepository;
    }    

}
