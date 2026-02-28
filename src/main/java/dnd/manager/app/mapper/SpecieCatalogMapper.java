package dnd.manager.app.mapper;

import dnd.manager.app.DTO.SpecieCatalogDTO;
import dnd.manager.app.model.SpecieCatalog;

public class SpecieCatalogMapper {
    public static SpecieCatalogDTO toDTO(SpecieCatalog s) {
        return new SpecieCatalogDTO(s.getId(), s.getName(), s.getSize(), s.getWalkSpeed(), s.getDescription(), s.getFlySpeed());
    }

    public static SpecieCatalog toEntity(SpecieCatalogDTO s) {
        SpecieCatalog entity = new SpecieCatalog();
        entity.setName(s.getName());
        entity.setSize(s.getSize());
        entity.setWalkSpeed(s.getWalkSpeed());
        entity.setDescription(s.getDescription());
        entity.setFlySpeed(s.getFlySpeed());
        return entity;
    }
}
