package dnd.manager.app.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import dnd.manager.app.model.ArmorType;
import dnd.manager.app.service.ArmorTypeService;

@Controller
@RequestMapping("/armorType")
public class ArmorTypeController {

    private final ArmorTypeService armorTypeService;

    public ArmorTypeController(ArmorTypeService armorTypeService) {
        this.armorTypeService = armorTypeService;
    }

    @GetMapping
    public String viewArmorTypes(Model model) {
        List<ArmorType> armorTypes = armorTypeService.findAll();
        model.addAttribute("armorTypes", armorTypes);
        return "armorTypes";
    }

    @GetMapping("/api")
    @ResponseBody
    public List<ArmorType> getAllArmorTypesJson() {
        return armorTypeService.findAll();
    }

    @GetMapping("/{id}")
    public String getArmorTypeById(@PathVariable Long id, Model model) {
        ArmorType armorType = armorTypeService.findById(id);
        if (armorType == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ArmorType not found with id: " + id);
        }
        model.addAttribute("armorType", armorType);
        return "armorTypeDetail";
    }
}