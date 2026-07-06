package dnd.manager.app.mapper;

import org.springframework.stereotype.Component;

import dnd.manager.app.dto.SpeciesDto.SpeciesResponseDto;
import dnd.manager.app.dto.SpeciesDto.SpeciesSummaryDto;
import dnd.manager.app.model.SpeciesEntities.Species;

@Component
public class SpeciesMapper {

    private final FeatureMapper featureMapper;
    
    SpeciesMapper(FeatureMapper featureMapper) {
        this.featureMapper = featureMapper;
    
    }

    public SpeciesSummaryDto toSummaryDto(Species e) {
        return new SpeciesSummaryDto(
            e.getId(),
            e.getName(),
            e.getSize(),
            e.getWalkSpeed()
        );
    }

    public SpeciesResponseDto toResponseDto(Species e) {
        return new SpeciesResponseDto(
            e.getName(),
            e.getDescription(),
            e.getSize(),
            e.getWalkSpeed(),
            e.getFlySpeed(),
            e.getFeatures().stream().map(f -> featureMapper.toDto(f)).toList()
        );
    }

}
