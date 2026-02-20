package dnd.manager.app.controller;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import dnd.manager.app.DTO.SpecieCatalogDTO;
import dnd.manager.app.service.SpecieCatalogService;


@RestController
@RequestMapping("/api/specie_catalog")
public class SpecieCatalogController {
    @Autowired
    private SpecieCatalogService service;

    @GetMapping
    public List<SpecieCatalogDTO> getAllCharacters() {
        return service.findAll();
    }

    @PostMapping()
    public List<SpecieCatalogDTO> insertSpecies(@RequestBody List<SpecieCatalogDTO> dtos) {
        return dtos.stream().map(service::insertSpecie).collect(Collectors.toList());
    }
}
