package dnd.manager.app.dto.ItemDto.WeaponDto;

import java.util.List;

public record WeaponSummaryDto(
    Long id,
    String name,
    Float weight,
    Integer price,
    String itemType,
    Boolean magic,
    Boolean attunement,
    String rarity,
    Integer rangeNormal,
    Integer rangeLong,
    String mastery,
    List<String> damageTypes
) {}
