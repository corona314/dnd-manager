package dnd.manager.app.dto.BackgroundDto;


import dnd.manager.app.dto.ItemDto.ItemSummaryDto;

public record BackgroundItemDto(
    ItemSummaryDto item,
    Integer quantity,
    String optionGroup,
    Boolean optional
) {}
