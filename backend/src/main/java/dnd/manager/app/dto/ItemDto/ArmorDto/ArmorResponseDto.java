package dnd.manager.app.dto.ItemDto.ArmorDto;

public record ArmorResponseDto(
    Integer acBase,
    Integer acMax,
    Integer strMin,
    Boolean stealthDis,
    String armorType
) {}
