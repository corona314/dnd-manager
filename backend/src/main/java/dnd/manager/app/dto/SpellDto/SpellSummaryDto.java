package dnd.manager.app.dto.SpellDto;

import java.util.List;

public record SpellSummaryDto(
    String name,
    Integer level,
    String school,
    String components,
    Boolean concentration,
    Boolean ritual,
    String savingThrowStat,
    Boolean attackRoll,
    List<SpellDamageTypeDto> damageTypes
) {}
