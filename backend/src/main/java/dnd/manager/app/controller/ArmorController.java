package dnd.manager.app.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import dnd.manager.app.model.ItemEntities.Item;
import dnd.manager.app.model.ItemEntities.ItemType;
import dnd.manager.app.model.ItemEntities.ArmorEntities.Armor;
import dnd.manager.app.model.ItemEntities.ArmorEntities.ArmorType;
import dnd.manager.app.service.ItemServices.ItemService;
import dnd.manager.app.service.ItemServices.ItemTypeService;
import dnd.manager.app.service.ItemServices.ArmorServices.ArmorService;
import dnd.manager.app.service.ItemServices.ArmorServices.ArmorTypeService;

@Controller
@RequestMapping("/armors")
public class ArmorController {

    private final ArmorService armorService;
    private final ArmorTypeService armorTypeService;
    private final ItemService objectService;
    private final ItemTypeService objectTypeService;

    public ArmorController(ArmorService armorService, ArmorTypeService armorTypeService, ItemService objectService, ItemTypeService objectTypeService) {
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
        armor.setObject(new Item());
        model.addAttribute("armor", armor);
        model.addAttribute("armorTypes", armorTypeService.findAll());
        model.addAttribute("objectTypes", objectTypeService.findAll());
        return "armorForm";
    }

    @PostMapping
    public String saveArmor(Armor armor) {
        Item item = armor.getObject();

        if (item != null && item.getObjectType() != null && item.getObjectType().getId() != null) {
            ItemType objectType = objectTypeService.findById(item.getObjectType().getId());
            item.setObjectType(objectType);
        }

        Item savedObject = null;
        if (item != null) {
            savedObject = objectService.save(item);
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