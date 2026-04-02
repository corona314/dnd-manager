package dnd.manager.app.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import dnd.manager.app.model.ArmorType;
import dnd.manager.app.service.ArmorTypeService;

@RestController
@RequestMapping("/armorType")
public class ArmorTypeController {

    private final ArmorTypeService armorTypeService;

    public ArmorTypeController(ArmorTypeService armorTypeService) {
        this.armorTypeService = armorTypeService;
    }

    @GetMapping
    public List<ArmorType> getAllArmorTypes() {
        return armorTypeService.findAll();
    }

    @GetMapping("/{id}")
    public ArmorType getArmorTypeById(@PathVariable Long id) {
        ArmorType armorType = armorTypeService.findById(id);
        if (armorType == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ArmorType not found with id: " + id);
        }
        return armorType;
    }
}