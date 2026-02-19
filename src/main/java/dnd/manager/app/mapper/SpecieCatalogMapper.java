package dnd.manager.app.mapper;

import dnd.manager.app.DTO.SpecieCatalogDTO;
import dnd.manager.app.model.SpecieCatalog;

public class SpecieCatalogMapper {
    public static SpecieCatalogDTO toDTO(SpecieCatalog s) {
        return new SpecieCatalogDTO(s.getId(), s.getName(), s.getSize(), s.getBase_speed(), s.getDescription());
    }

    public static SpecieCatalog toEntity(SpecieCatalogDTO s) {
        return new SpecieCatalog(s.getId(), s.getName(), s.getSize(), s.getBase_speed(), s.getDescription());
    }
}
