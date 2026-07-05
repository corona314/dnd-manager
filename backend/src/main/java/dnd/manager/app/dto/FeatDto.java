package dnd.manager.app.dto;

public record FeatDto(
    Long id,
    String name,
    String description,
    String prerequisite,
    Boolean repeatable,
    String category
) {}
