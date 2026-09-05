package dnd.manager.app.dto;

public record FeatureChoiceDto(
    Long id,
    String name,
    String description,
    Integer level,
    String prerequisite
) {}