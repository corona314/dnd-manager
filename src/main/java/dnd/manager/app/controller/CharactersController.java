package dnd.manager.app.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import dnd.manager.app.DTO.CharactersDTO;
import dnd.manager.app.service.CharactersService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/api/characters")

public class CharactersController {

    @Autowired
    private CharactersService service;

    @GetMapping
    public List<CharactersDTO> getAllCharacters() {
        return service.findAll();
    }

    @PostMapping
    public CharactersDTO insertCharacter(@RequestBody CharactersDTO dto) {
        return service.insertCharacter(dto);
    }
    
}
