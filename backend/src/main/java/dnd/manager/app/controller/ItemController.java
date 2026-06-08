package dnd.manager.app.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dnd.manager.app.dto.ItemDto.ItemResponseDto;
import dnd.manager.app.dto.ItemDto.ItemSummaryDto;
import dnd.manager.app.service.ItemServices.ItemService;

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
        @RequestParam(required = false) Float weightMin,
        @RequestParam(required = false) Float weightMax,
        @RequestParam(required = false) Integer priceMin,
        @RequestParam(required = false) Integer priceMax,
        @RequestParam(required = false) String itemType,
        @RequestParam(required = false) Boolean magic,
        @RequestParam(required = false) Boolean attunement,
        @RequestParam(required = false) String rarity,
        @RequestParam(defaultValue = "0")  int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(service.findItems(name, weightMin, weightMax, priceMin, priceMax, itemType, magic, attunement, rarity, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemResponseDto> item(@PathVariable Long id){
        return ResponseEntity.ok(service.findById(id));
    }

}
