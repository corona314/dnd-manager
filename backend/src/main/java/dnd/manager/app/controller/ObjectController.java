package dnd.manager.app.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import dnd.manager.app.model.ItemEntities.Item;
import dnd.manager.app.service.ItemServices.ItemService;
import dnd.manager.app.service.ItemServices.ItemTypeService;

@Controller
@RequestMapping("/item")
public class ObjectController {

    private final ItemService objectService;
    private final ItemTypeService objectTypeService;

    public ObjectController(ItemService objectService, ItemTypeService objectTypeService) {
        this.objectService = objectService;
        this.objectTypeService = objectTypeService;
    }

    @GetMapping
    public String listObjects(Model model) {
        List<Item> item = objectService.findAll();
        model.addAttribute("item", item);
        return "item";
    }

    @GetMapping("/new")
    public String createObjectForm(Model model) {
        model.addAttribute("objectEntity", new Item());
        model.addAttribute("objectTypes", objectTypeService.findAll());
        return "objectForm";
    }

    @PostMapping
    public String saveObject(Item objectEntity) {
        objectService.save(objectEntity);
        return "redirect:/item";
    }

    @PostMapping("/{id}/delete")
    public String deleteObject(@PathVariable Long id) {
        objectService.deleteById(id);
        return "redirect:/item";
    }
}