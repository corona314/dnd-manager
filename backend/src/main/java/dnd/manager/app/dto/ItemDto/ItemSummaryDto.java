package dnd.manager.app.dto.ItemDto;

public record ItemSummaryDto(
    String name,
    Float weight,
    Integer price,
    String itemType,
    Boolean magic,
    Boolean attunement,
    String rarity
) {}
