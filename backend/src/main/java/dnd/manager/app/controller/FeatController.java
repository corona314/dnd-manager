package dnd.manager.app.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dnd.manager.app.dto.FeatDto;
import dnd.manager.app.service.FeatService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/api/feats")
public class FeatController {
    
    private final FeatService service;


    FeatController(FeatService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<Page<FeatDto>> feats(
        @RequestParam(required = false) String name,
        @RequestParam(required = false) String repeatable,
        @PageableDefault(size = 20, page = 0) Pageable pageable
    ) {
        return ResponseEntity.ok(service.findFeats(name, repeatable, pageable));
    }

    
    @GetMapping("/{id}")
    public ResponseEntity<FeatDto> feat(@PathVariable Long id){
        return ResponseEntity.ok(service.findById(id));
    }
}
