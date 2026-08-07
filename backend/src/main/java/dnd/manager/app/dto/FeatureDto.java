package dnd.manager.app.dto;

import java.util.List;

public record FeatureDto(
    String name,
    String description,
    String featureType,
    List<FeatureChoiceDto> choices
) {}
