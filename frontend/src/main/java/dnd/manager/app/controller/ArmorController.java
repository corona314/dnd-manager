package dnd.manager.app.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import dnd.manager.app.model.Armor;
import dnd.manager.app.model.ArmorType;
import dnd.manager.app.model.ObjectEntity;
import dnd.manager.app.model.ObjectType;
import dnd.manager.app.service.ArmorService;
import dnd.manager.app.service.ArmorTypeService;
import dnd.manager.app.service.ObjectService;
import dnd.manager.app.service.ObjectTypeService;

@Controller
@RequestMapping("/armors")
public class ArmorController {

    private final ArmorService armorService;
    private final ArmorTypeService armorTypeService;
    private final ObjectService objectService;
    private final ObjectTypeService objectTypeService;

    public ArmorController(ArmorService armorService, ArmorTypeService armorTypeService, ObjectService objectService, ObjectTypeService objectTypeService) {
        this.armorService = armorService;
        this.armorTypeService = armorTypeService;
        this.objectService = objectService;
        this.objectTypeService = objectTypeService;
    }

    @GetMapping
    public String listArmors(Model model) {
        List<Armor> armors = armorService.findAll();
        model.addAttribute("armors", armors);
        return "armors";
    }

    @GetMapping("/new")
    public String createArmorForm(Model model) {
        Armor armor = new Armor();
        armor.setObject(new ObjectEntity());
        model.addAttribute("armor", armor);
        model.addAttribute("armorTypes", armorTypeService.findAll());
        model.addAttribute("objectTypes", objectTypeService.findAll());
        return "armorForm";
    }

    @PostMapping
    public String saveArmor(Armor armor) {
        ObjectEntity object = armor.getObject();

        if (object != null && object.getObjectType() != null && object.getObjectType().getId() != null) {
            ObjectType objectType = objectTypeService.findById(object.getObjectType().getId());
            object.setObjectType(objectType);
        }

        ObjectEntity savedObject = null;
        if (object != null) {
            savedObject = objectService.save(object);
            armor.setObject(savedObject);
        }

        if (armor.getArmorType() != null && armor.getArmorType().getId() != null) {
            ArmorType type = armorTypeService.findById(armor.getArmorType().getId());
            armor.setArmorType(type);
        }

        armorService.save(armor);
        return "redirect:/armors";
    }

    @PostMapping("/{id}/delete")
    public String deleteArmor(@PathVariable Long id) {
        armorService.deleteById(id);
        return "redirect:/armors";
    }
}