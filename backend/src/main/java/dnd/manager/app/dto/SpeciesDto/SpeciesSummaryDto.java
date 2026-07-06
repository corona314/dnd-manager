package dnd.manager.app.dto.SpeciesDto;

public record SpeciesSummaryDto(
    Long id,
    String name,
    String size,
    Integer walkSpeed
) {}
