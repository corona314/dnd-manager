package dnd.manager.app.dto.ItemDto.WeaponDto;

public record WeaponDamageDto(
    String damageRoll,
    String damageType,
    Boolean always
) {}
