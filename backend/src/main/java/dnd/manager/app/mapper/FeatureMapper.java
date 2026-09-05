package dnd.manager.app.mapper;

import org.springframework.stereotype.Component;

import dnd.manager.app.dto.FeatureChoiceDto;
import dnd.manager.app.dto.FeatureDto;
import dnd.manager.app.model.FeatureEntities.Feature;
import dnd.manager.app.model.FeatureEntities.FeatureChoice;

@Component
public class FeatureMapper {

    public FeatureDto toDto(Feature e) {
        return new FeatureDto(
            e.getName(),
            e.getDescription(),
            e.getFeatureType() == null ? null : e.getFeatureType().getName(),
            e.getChoices().stream().map(this::toFeatureChoiceDto).toList()
        );
    }

    private FeatureChoiceDto toFeatureChoiceDto (FeatureChoice e) {
        return new FeatureChoiceDto(
            e.getChoice().getId(),
            e.getChoice().getName(),
            e.getChoice().getDescription(),
            e.getLevel(),
            e.getPrerequisite()
        );
    }
}
