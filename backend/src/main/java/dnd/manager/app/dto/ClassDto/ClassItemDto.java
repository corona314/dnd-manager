package dnd.manager.app.dto.ClassDto;


import dnd.manager.app.dto.ItemDto.ItemSummaryDto;

public record ClassItemDto(
    ItemSummaryDto item,
    Integer quantity,
    String optionGroup,
    Boolean optional
) {}
