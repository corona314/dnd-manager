package dnd.manager.app.mapper;

import dnd.manager.app.DTO.SpecieCatalogDTO;
import dnd.manager.app.model.SpecieCatalog;

public class SpecieCatalogMapper {
    public static SpecieCatalogDTO toDTO(SpecieCatalog s) {
        return new SpecieCatalogDTO(s.getId(), s.getName(), s.getSize(), s.getBase_speed(), s.getDescription());
    }

    public static SpecieCatalog toEntity(SpecieCatalogDTO s) {
        SpecieCatalog entity = new SpecieCatalog();
        entity.setName(s.getName());
        entity.setSize(s.getSize());
        entity.setBase_speed(s.getBase_speed());
        entity.setDescription(s.getDescription());
        return entity;
    }
}
