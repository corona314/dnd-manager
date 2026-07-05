package dnd.manager.app.mapper;

import org.springframework.stereotype.Component;

import dnd.manager.app.dto.FeatDto;
import dnd.manager.app.model.Feat;

@Component
public class FeatMapper {

    public FeatDto toDto(Feat e) {
        return new FeatDto(
            e.getId(),
            e.getName(),
            e.getDescription(),
            e.getPrerequisite(),
            e.getRepeatable(),
            e.getFeatCategory()
        );
    }
}
