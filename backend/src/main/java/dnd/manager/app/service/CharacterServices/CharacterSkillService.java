package dnd.manager.app.service.CharacterServices;

import java.util.List;
import org.springframework.stereotype.Service;

import dnd.manager.app.model.CharacterEntities.CharacterSkill;
import dnd.manager.app.repository.CharacterRepositories.CharacterSkillRepository;

@Service
public class CharacterSkillService {

    private final CharacterSkillRepository repository;

    public CharacterSkillService(CharacterSkillRepository repository) {
        this.repository = repository;
    }

    public List<CharacterSkill> findAll() {
        return repository.findAll();
    }

    public List<CharacterSkill> findByCharacterId(Long characterId){
        return repository.findByCharacterId(characterId);
    }

    public CharacterSkill save(CharacterSkill characterAbility) {
        return repository.save(characterAbility);
    }

}
