package dnd.manager.app.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import dnd.manager.app.model.ObjectEntity;
import dnd.manager.app.service.ObjectService;
import dnd.manager.app.service.ObjectTypeService;

@Controller
@RequestMapping("/objects")
public class ObjectController {

    private final ObjectService objectService;
    private final ObjectTypeService objectTypeService;

    public ObjectController(ObjectService objectService, ObjectTypeService objectTypeService) {
        this.objectService = objectService;
        this.objectTypeService = objectTypeService;
    }

    @GetMapping
    public String listObjects(Model model) {
        List<ObjectEntity> objects = objectService.findAll();
        model.addAttribute("objects", objects);
        return "objects";
    }

    @GetMapping("/new")
    public String createObjectForm(Model model) {
        model.addAttribute("objectEntity", new ObjectEntity());
        model.addAttribute("objectTypes", objectTypeService.findAll());
        return "objectForm";
    }

    @PostMapping
    public String saveObject(ObjectEntity objectEntity) {
        objectService.save(objectEntity);
        return "redirect:/objects";
    }

    @PostMapping("/{id}/delete")
    public String deleteObject(@PathVariable Long id) {
        objectService.deleteById(id);
        return "redirect:/objects";
    }
}