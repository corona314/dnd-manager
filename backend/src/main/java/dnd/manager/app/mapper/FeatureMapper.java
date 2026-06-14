package dnd.manager.app.mapper;

import org.springframework.stereotype.Component;

import dnd.manager.app.dto.FeatureDto;
import dnd.manager.app.model.FeatureEntities.Feature;

@Component
public class FeatureMapper {

    public FeatureDto toDto(Feature e) {
        return new FeatureDto(
            e.getId(),
            e.getName(),
            e.getDescription(),
            e.getFeatureType() == null ? null : e.getFeatureType().getName()
        );
    }
}
