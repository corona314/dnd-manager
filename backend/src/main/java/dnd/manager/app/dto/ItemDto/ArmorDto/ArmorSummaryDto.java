package dnd.manager.app.dto.ItemDto.ArmorDto;

public record ArmorSummaryDto(
    Long id,
    String name,
    Float weight,
    Integer price,
    String itemType,
    Boolean magic,
    Boolean attunement,
    String rarity,
    Integer acBase,
    Integer acMax,
    Integer strMin,
    Boolean stealthDis,
    String armorType
) {}
