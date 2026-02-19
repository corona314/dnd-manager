package dnd.manager.app.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dnd.manager.app.DTO.SpecieCatalogDTO;
import dnd.manager.app.mapper.SpecieCatalogMapper;
import dnd.manager.app.repository.SpecieCatalogRepository;

@RestController
@RequestMapping("/api/specie_catalog")
public class SpecieCatalogController {
    @Autowired
    private SpecieCatalogRepository repository;

    @GetMapping
    public List<SpecieCatalogDTO> getAllCharacters() {
        return repository.findAll().stream().map(SpecieCatalogMapper::toDTO).collect(Collectors.toList());
    }

}
