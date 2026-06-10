package dnd.manager.app.dto.ItemDto;

public record ShieldSummaryDto(
    Long id,
    String name,
    Float weight,
    Integer price,
    String itemType,
    Boolean magic,
    Boolean attunement,
    String rarity,
    Integer acBonus
) {}
