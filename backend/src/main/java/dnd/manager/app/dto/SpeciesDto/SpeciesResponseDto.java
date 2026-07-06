package dnd.manager.app.dto.SpeciesDto;

import java.util.List;

import dnd.manager.app.dto.FeatureDto;

public record SpeciesResponseDto(
    String name,
    String description,
    String size,
    Integer walkSpeed,
    Integer flySpeed,
    List<FeatureDto> features
) {}
