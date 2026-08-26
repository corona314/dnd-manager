package dnd.manager.app.dto.CharacterDto;

public record CharacterSpellSlotResponseDto(
    Integer spellLevel,
    Integer currentSlots,
    Integer maxSlots
) {}
