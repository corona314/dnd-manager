package dnd.manager.app.dto.CharacterDto;

import dnd.manager.app.dto.ItemDto.ItemSummaryDto;

public record CharacterItemDto(
    ItemSummaryDto item,
    Integer quantity,
    Boolean equipped,
    Boolean attuned
) {}
