package dnd.manager.app.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dnd.manager.app.dto.CharacterDto.CharacterCreateDto;
import dnd.manager.app.dto.CharacterDto.CharacterPatchDto;
import dnd.manager.app.dto.CharacterDto.CharacterResponseDto;
import dnd.manager.app.dto.CharacterDto.CharacterStatDto;
import dnd.manager.app.dto.CharacterDto.CharacterSummaryDto;
import dnd.manager.app.model.User;
import dnd.manager.app.service.CharacterServices.CharacterService;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/characters")
public class CharacterController {
    
    private final CharacterService service;

    public CharacterController(CharacterService service){
        this.service  = service;
    }

    @GetMapping("/me")
    public ResponseEntity<List<CharacterSummaryDto>> getMyCharacters(@AuthenticationPrincipal User user){
        return ResponseEntity.ok(service.findByUserId(user.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CharacterResponseDto> getById(@AuthenticationPrincipal User user, @PathVariable Long id) {
        return ResponseEntity.ok(service.findByUserIdAndId(user.getId(), id));
    }
    
    @PostMapping
    public ResponseEntity<CharacterResponseDto> create(@AuthenticationPrincipal User user, @RequestBody CharacterCreateDto dto) {
        
        return ResponseEntity.ok(service.create(user.getId(), dto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CharacterResponseDto> patch(@AuthenticationPrincipal User user, @PathVariable Long id, @RequestBody CharacterPatchDto dto) {
        return ResponseEntity.ok(service.patch(user.getId(), id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal User user, @PathVariable Long id) {
        service.delete(user.getId(), id);
        return ResponseEntity.noContent().build();
    }    

    // Replace
    @PatchMapping("/{id}/stats")
    public ResponseEntity<CharacterResponseDto> replaceStats(@AuthenticationPrincipal User user, @PathVariable Long id, @RequestBody List<CharacterStatDto> dto) {
        return ResponseEntity.ok(service.replaceStats(user.getId(), id, dto));
    }
}
