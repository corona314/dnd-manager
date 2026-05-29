package dnd.manager.app.dto.CharacterDto;


public record CharacterItemDto(
    Integer quantity,
    Boolean equipped,
    Boolean attuned
) {}
