package dnd.manager.app.dto.SpellDto;

public record SpellSummaryDto(
    String name,
    Integer level,
    String school,
    String components,
    Boolean concentration,
    Boolean ritual,
    String savingThrowStat,
    Boolean attackRoll,
    String damageType
) {}
