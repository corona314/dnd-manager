package dnd.manager.app.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dnd.manager.app.dto.ItemDto.ItemResponseDto;
import dnd.manager.app.dto.ItemDto.ItemSummaryDto;
import dnd.manager.app.service.ItemServices.ItemService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/api/items")
public class ItemController {
    
    @Autowired
    private ItemService service;


    @GetMapping
    public ResponseEntity<Page<ItemSummaryDto>> items(
        @RequestParam(required = false) String name,
        @RequestParam(required = false) Integer level,
        @RequestParam(required = false) Integer schoolId,
        @RequestParam(required = false) String components,
        @RequestParam(required = false) Boolean concentration,
        @RequestParam(required = false) Boolean ritual,
        @RequestParam(required = false) String savingThrowStat,
        @RequestParam(required = false) Boolean attackRoll,
        @RequestParam(required = false) List<String> damageTypes,
        @RequestParam(defaultValue = "0")  int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        return null; //ResponseEntity.ok(service.findSpells(name, level, schoolId, components, concentration, ritual, savingThrowStat, attackRoll, damageTypes, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemResponseDto> spell(@PathVariable Long id){
        return null; //ResponseEntity.ok(service.findById(id));
    }

}
