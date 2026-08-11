package dnd.manager.app.dto.CharacterDto;

public record CharacterResourceDto(
    String name,
    String className,
    Integer currentValue,
    Integer maxValue
) {}
