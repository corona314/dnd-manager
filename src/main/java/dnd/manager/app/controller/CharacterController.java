package dnd.manager.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import dnd.manager.app.service.CharacterService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import dnd.manager.app.model.Characters;

@Controller
public class CharacterController {
    private final CharacterService service;
    public CharacterController(CharacterService service){ this.service = service; }
    
   

    @GetMapping("/characters")
    public String showCharacters(Model model){
        model.addAttribute("characters", service.getAll());
        model.addAttribute("character", new Characters());
        return "characters";
    }

    @PostMapping("/characters/save")
    public String saveCharacter(@ModelAttribute Characters character){
        service.save(character);
        return "redirect:/characters";
    }
}
