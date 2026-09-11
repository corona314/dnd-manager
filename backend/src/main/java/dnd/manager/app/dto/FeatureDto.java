package dnd.manager.app.dto;

import java.util.List;

public record FeatureDto(
    Long id,
    String name,
    String description,
    String featureType,
    List<FeatureChoiceDto> choices
) {}
