package dnd.manager.app.dto.SpellDto;


public record SpellResponseDto(
    String name,
    Integer level,
    String school,
    String castingTime,
    String range,
    String duration,
    String components,
    String material,
    Boolean concentration,
    Boolean ritual,
    String description,
    String savingThrowStat,
    Boolean attackRoll,
    String damageRoll,
    String damageType
) {}
