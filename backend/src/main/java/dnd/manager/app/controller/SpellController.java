package dnd.manager.app.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dnd.manager.app.dto.SpellDto.SpellResponseDto;
import dnd.manager.app.dto.SpellDto.SpellSummaryDto;
import dnd.manager.app.service.SpellServices.SpellService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/api/spells")
public class SpellController {
    
    @Autowired
    private SpellService service;


    @GetMapping
    public ResponseEntity<Page<SpellSummaryDto>> spells(
        @RequestParam(required = false) String name,
        @RequestParam(required = false) Integer level,
        @RequestParam(required = false) Integer schoolId,
        @RequestParam(required = false) String components,
        @RequestParam(required = false) Integer concentration,
        @RequestParam(required = false) Integer ritual,
        @RequestParam(required = false) String savingThrowStat,
        @RequestParam(required = false) Boolean attackRoll,
        @RequestParam(required = false) String damageType,
        @RequestParam(defaultValue = "0")  int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(service.findSpells(name, level, schoolId, components, concentration, ritual, savingThrowStat, attackRoll, damageType, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SpellResponseDto> spell(@PathVariable Long id){
        return ResponseEntity.ok(service.findById(id));
    }

}
