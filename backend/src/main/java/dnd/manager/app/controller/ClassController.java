package dnd.manager.app.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dnd.manager.app.dto.ClassDto.ClassResponseDto;
import dnd.manager.app.dto.ClassDto.ClassSummaryDto;
import dnd.manager.app.service.ClassServices.ClassService;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/api/classes")
public class ClassController {
    
    private final ClassService service;


    ClassController(ClassService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<Page<ClassSummaryDto>> classes(
        @RequestParam(required = false) String name,
        @RequestParam(required = false) List<String> hitPointDie,
        @RequestParam(defaultValue = "0")  int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(service.findClasses(name, hitPointDie, page, size));
    }

    
    @GetMapping("/{id}")
    public ResponseEntity<ClassResponseDto> classEntity(@PathVariable Long id){
        return ResponseEntity.ok(service.findById(id));
    }


}
