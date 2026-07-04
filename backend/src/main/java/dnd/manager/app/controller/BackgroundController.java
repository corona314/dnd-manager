package dnd.manager.app.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dnd.manager.app.dto.BackgroundDto.BackgroundResponseDto;
import dnd.manager.app.dto.BackgroundDto.BackgroundSummaryDto;
import dnd.manager.app.service.BackgroundServices.BackgroundService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/api/backgrounds")
public class BackgroundController {
    
    private final BackgroundService service;


    BackgroundController(BackgroundService service) {
        this.service = service;
    }


    @GetMapping
    public ResponseEntity<Page<BackgroundSummaryDto>> backgrounds(
        @RequestParam(required = false) String name,
        @PageableDefault(size = 20, page = 0) Pageable pageable
    ) {
        return ResponseEntity.ok(service.findBackgrounds(name, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BackgroundResponseDto> background(@PathVariable Long id){
        return ResponseEntity.ok(service.findById(id));
    }

}
