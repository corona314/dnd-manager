package dnd.manager.app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import dnd.manager.app.model.Skill;
import dnd.manager.app.repository.SkillRepository;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/api/skills")
public class SkillController {

    @Autowired
    private SkillRepository repository;
    
    @GetMapping
    public List<Skill> getAllSkills() {
        return (List<Skill>) repository.findAll();
    }
    
}
