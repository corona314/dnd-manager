package dnd.manager.app.dto;

public record FeatureDto(
    Long id,
    String name,
    String description,
    String featureType
) {}
