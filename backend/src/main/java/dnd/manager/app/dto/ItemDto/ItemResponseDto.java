package dnd.manager.app.dto.ItemDto;

import java.util.List;

import dnd.manager.app.dto.ItemDto.ArmorDto.ArmorResponseDto;
import dnd.manager.app.dto.ItemDto.WeaponDto.WeaponResponseDto;

public record ItemResponseDto(
    String name,
    Float weight,
    Integer price,
    String itemType,
    Boolean magic,
    Boolean attunement,
    String rarity,
    String description,

    List<ItemFeatureDto> features,

    ArmorResponseDto armorDto,
    WeaponResponseDto weaponDto,
    ShieldResponseDto shieldDto
) {}
