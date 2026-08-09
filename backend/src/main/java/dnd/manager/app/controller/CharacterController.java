package dnd.manager.app.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dnd.manager.app.dto.CharacterDto.CharacterCreateDto;
import dnd.manager.app.dto.CharacterDto.CharacterFeatDto;
import dnd.manager.app.dto.CharacterDto.CharacterItemDto;
import dnd.manager.app.dto.CharacterDto.CharacterPatchDto;
import dnd.manager.app.dto.CharacterDto.CharacterResponseDto;
import dnd.manager.app.dto.CharacterDto.CharacterSkillDto;
import dnd.manager.app.dto.CharacterDto.CharacterSpellDto;
import dnd.manager.app.dto.CharacterDto.CharacterAbilityDto;
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
    public ResponseEntity<CharacterSummaryDto> create(@AuthenticationPrincipal User user, @RequestBody CharacterCreateDto dto) {
        
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


    //Replaces
    @PatchMapping("/{id}/abilities")
    public ResponseEntity<CharacterResponseDto> replaceAbilities(
        @AuthenticationPrincipal User user, 
        @PathVariable Long id, 
        @RequestBody List<CharacterAbilityDto> dtos
    ) {
        return ResponseEntity.ok(service.replaceAbilities(user.getId(), id, dtos));
    }

    @PatchMapping("/{id}/skills")
    public ResponseEntity<CharacterResponseDto> replaceSkills(@AuthenticationPrincipal User user, @PathVariable Long id, @RequestBody List<CharacterSkillDto> dto) {
        return ResponseEntity.ok(service.replaceSkills(user.getId(), id, dto));
    }


    //Items

    @PostMapping("/{id}/items/{itemId}/buy")
    public ResponseEntity<CharacterResponseDto> buyItem(
        @AuthenticationPrincipal User user, 
        @PathVariable Long id, 
        @PathVariable Long itemId,
        @RequestBody(required = false) CharacterItemDto dto
    ) {
        return ResponseEntity.ok(service.buyItem(user.getId(), id, itemId, dto));
    }

    @PostMapping("/{id}/items/{itemId}")
    public ResponseEntity<CharacterResponseDto> addItem(
        @AuthenticationPrincipal User user, 
        @PathVariable Long id, 
        @PathVariable Long itemId,
        @RequestBody(required = false) CharacterItemDto dto
    ) {
        return ResponseEntity.ok(service.addItem(user.getId(), id, itemId, dto));
    }

    @PatchMapping("/{id}/items/{itemId}/buy")
    public ResponseEntity<CharacterResponseDto> updateItem(
        @AuthenticationPrincipal User user,
        @PathVariable Long id,
        @PathVariable Long itemId,
        @RequestBody CharacterItemDto dto
    ) {
        return ResponseEntity.ok(service.updateItem(user.getId(), id, itemId, dto));
    }

    @DeleteMapping("/{id}/items/{itemId}")
    public ResponseEntity<CharacterResponseDto> removeItem(
        @AuthenticationPrincipal User user,
        @PathVariable Long id,
        @PathVariable Long itemId,
        @RequestParam(defaultValue = "1") Integer quantity
    ) {
        return ResponseEntity.ok(service.removeItem(user.getId(), id, itemId, quantity));

    }


    // Spells

    @PostMapping("/{id}/spells/{spellId}")
    public ResponseEntity<CharacterResponseDto> addSpell(
        @AuthenticationPrincipal User user,
        @PathVariable Long id,
        @PathVariable Long spellId,
        @RequestBody(required = false) CharacterSpellDto dto
    ) {
        return ResponseEntity.ok(service.addSpell(user.getId(), id, spellId, dto));
    }

    @PatchMapping("/{id}/spells/{spellId}")
    public ResponseEntity<CharacterResponseDto> updateSpell(
        @AuthenticationPrincipal User user,
        @PathVariable Long id,
        @PathVariable Long spellId,
        @RequestBody CharacterSpellDto dto
    ) {
        return ResponseEntity.ok(service.updateSpell(user.getId(), id, spellId, dto));
    }

    @DeleteMapping("/{id}/spells/{spellId}")
    public ResponseEntity<CharacterResponseDto> removeSpell(
        @AuthenticationPrincipal User user,
        @PathVariable Long id,
        @PathVariable Long spellId
    ) {
        return ResponseEntity.ok(service.removeSpell(user.getId(), id, spellId));
    }


    @PostMapping("/{id}/feats/{featId}")
    public ResponseEntity<CharacterResponseDto> addFeat(
        @AuthenticationPrincipal User user,
        @PathVariable Long id,
        @PathVariable Long featId,
        @RequestBody(required = false) CharacterFeatDto dto
    ) {
        return ResponseEntity.ok(service.addFeat(user.getId(), id, featId, dto));
    }

    @DeleteMapping("/{id}/feats/{featId}")
    public ResponseEntity<CharacterResponseDto> removeFeat(
        @AuthenticationPrincipal User user,
        @PathVariable Long id,
        @PathVariable Long featId
    ) {
        return ResponseEntity.ok(service.removeFeat(user.getId(), id, featId));
    }





    @PatchMapping("/{id}/finalize")
    public ResponseEntity<CharacterResponseDto> finalize(@AuthenticationPrincipal User user, @PathVariable Long id){
        return ResponseEntity.ok(service.finalize(user.getId(), id));
    }

}
