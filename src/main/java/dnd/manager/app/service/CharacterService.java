package dnd.manager.app.service;

import java.util.List;
import org.springframework.stereotype.Service;

import dnd.manager.app.model.Characters;
import dnd.manager.app.repository.CharacterRepository;

@Service
public class CharacterService {
    
    private final CharacterRepository repo;
    
    public CharacterService(CharacterRepository repo) { 
        this.repo = repo; 
    }
    
    public List<Characters> getAll() { 
        return repo.findAll(); 
    }
    
    public Characters save(Characters c) { 
        return repo.save(c); 
    }
}
