package dnd.manager.app.dto;

public record FeatureChoiceDto(
    String name,
    String description,
    Integer level,
    String prerequisite
) {}