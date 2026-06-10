package dnd.manager.app.dto.ItemDto.WeaponDto;

import java.util.List;

public record WeaponResponseDto(
    Integer rangeNormal,
    Integer rangeLong,
    String weaponCategory,
    String weaponType,

    MasteryDto mastery,
    List<WeaponDamageDto> damages,
    List<WeaponWeaponPropertyDto> properties
) {}