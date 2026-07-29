package dnd.manager.app.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dnd.manager.app.dto.SkillDto;
import dnd.manager.app.service.SkillService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/api/skills")
public class SkillController {
    
    private final SkillService service;


    SkillController(SkillService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<Page<SkillDto>> skills(
        @RequestParam(required = false) String name,
        @RequestParam(required = false) String ability,
        @PageableDefault(size = 20, page = 0) Pageable pageable
    ) {
        return ResponseEntity.ok(service.findSkills(name, ability, pageable));
    }

    
    @GetMapping("/{id}")
    public ResponseEntity<SkillDto> skill(@PathVariable Long id){
        return ResponseEntity.ok(service.findById(id));
    }

}
