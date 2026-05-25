package dnd.manager.app.service.CharacterServices;

import java.util.List;
import org.springframework.stereotype.Service;

import dnd.manager.app.model.CharacterEntities.CharacterStat;
import dnd.manager.app.repository.CharacterRepositories.CharacterStatRepository;

@Service
public class CharacterStatService {

    private final CharacterStatRepository repository;

    public CharacterStatService(CharacterStatRepository repository) {
        this.repository = repository;
    }

    public List<CharacterStat> findAll() {
        return repository.findAll();
    }

    public List<CharacterStat> findByCharacterId(Long characterId){
        return repository.findByCharacterId(characterId);
    }

    public CharacterStat save(CharacterStat characterStat) {
        return repository.save(characterStat);
    }

}
