package dnd.manager.app.service;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import dnd.manager.app.DTO.CharactersDTO;
import dnd.manager.app.mapper.CharactersMapper;
import dnd.manager.app.repository.CharactersRepository;

@Service
public class CharactersService {
    
    @Autowired
    private CharactersRepository repo;
    
    public List<CharactersDTO> findAll() { 
        return repo.findAll().stream().map(CharactersMapper::toDTO).collect(Collectors.toList());
    }

    public CharactersDTO insertCharacter(CharactersDTO dto) {
        var entity = CharactersMapper.toEntity(dto);
        var savedEntity = repo.save(entity);
        return CharactersMapper.toDTO(savedEntity);
    }
}
