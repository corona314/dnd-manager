package dnd.manager.app.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import dnd.manager.app.dto.SpellDto.SpellResponseDto;
import dnd.manager.app.dto.SpellDto.SpellSummaryDto;
import dnd.manager.app.service.SpellServices.SpellService;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/api/spells")
public class SpellController {
    
    private final SpellService service;


    SpellController(SpellService service) {
        this.service = service;
    }


    @GetMapping
    public ResponseEntity<Page<SpellSummaryDto>> spells(
        @RequestParam(required = false) String name,
        @RequestParam(required = false) Integer levelMin,
        @RequestParam(required = false) Integer levelMax,
        @RequestParam(required = false) List<Integer> schoolId,
        @RequestParam(required = false) String components,
        @RequestParam(required = false) Boolean concentration,
        @RequestParam(required = false) Boolean ritual,
        @RequestParam(required = false) String savingThrowAbility,
        @RequestParam(required = false) Boolean attackRoll,
        @RequestParam(required = false) List<String> damageType,
        @RequestParam(defaultValue = "0")  int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(service.findSpells(name, levelMin, levelMax, schoolId, components, concentration, ritual, savingThrowAbility, attackRoll, damageType, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SpellResponseDto> spell(@PathVariable Long id){
        return ResponseEntity.ok(service.findById(id));
    }

}
