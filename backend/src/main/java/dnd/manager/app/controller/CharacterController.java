package dnd.manager.app.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dnd.manager.app.dto.CharacterDto.CharacterCreateDto;
import dnd.manager.app.dto.CharacterDto.CharacterPatchDto;
import dnd.manager.app.dto.CharacterDto.CharacterResponseDto;
import dnd.manager.app.dto.CharacterDto.CharacterSummaryDto;
import dnd.manager.app.mapper.CharacterMapper;
import dnd.manager.app.model.CharacterEntities.CharacterEntity;
import dnd.manager.app.service.CharacterServices.CharacterService;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;


@RestController
@RequestMapping("/api/characters")
public class CharacterController {
    private final CharacterService service;

    public CharacterController(CharacterService service){
        this.service  = service;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CharacterSummaryDto>> getByUserId(@PathVariable Long userId){
        return ResponseEntity.ok(service.findByUserId(userId));
    }

    @GetMapping("/user/{userId}/{id}")
    public ResponseEntity<CharacterResponseDto> getMethodName(@PathVariable Long userId, @PathVariable Long id) {
        return ResponseEntity.ok(service.findByUserIdAndId(userId, id));
    }
    
    @PostMapping
    public ResponseEntity<CharacterResponseDto> create(@RequestBody CharacterCreateDto dto) {
        
        return ResponseEntity.ok(service.create(dto));
    }

    @PatchMapping("/user/{userId}/{id}")
    public ResponseEntity<CharacterResponseDto> patch(@PathVariable Long userId, @PathVariable Long id, @RequestBody CharacterPatchDto dto) {
        return ResponseEntity.ok(service.patch(userId, id, dto));
    }
    
}
