package dnd.manager.app.dto.SpellDto;

public record SpellUpcastDto (
    Integer level,
    String upcastType,
    String damageRoll,
    String description
){}
