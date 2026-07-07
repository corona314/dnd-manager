package dnd.manager.app.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dnd.manager.app.dto.SpeciesDto.SpeciesResponseDto;
import dnd.manager.app.dto.SpeciesDto.SpeciesSummaryDto;
import dnd.manager.app.service.SpeciesServices.SpeciesService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/api/species")
public class SpeciesController {
    
    private final SpeciesService service;


    SpeciesController(SpeciesService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<Page<SpeciesSummaryDto>> species(
        @RequestParam(required = false) String name,
        @RequestParam(required = false) String sizeS,
        @RequestParam(required = false) Integer walkSpeed,
        @PageableDefault(size = 20, page = 0) Pageable pageable
    ) {
        return ResponseEntity.ok(service.findSpecies(name, sizeS, walkSpeed, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SpeciesResponseDto> species(@PathVariable Long id){
        return ResponseEntity.ok(service.findById(id));
    }

}
