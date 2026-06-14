package dnd.manager.app.dto.SpellDto;

import java.util.List;

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
    String savingThrowAbility,
    Boolean attackRoll,
    String damageRoll,
    List<SpellDamageTypeDto> damageTypes
) {}
