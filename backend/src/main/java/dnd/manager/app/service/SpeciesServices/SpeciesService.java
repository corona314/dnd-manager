package dnd.manager.app.service.SpeciesServices;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import dnd.manager.app.repository.SpeciesRepositories.SpeciesRepository;
import dnd.manager.app.dto.SpeciesDto.SpeciesResponseDto;
import dnd.manager.app.dto.SpeciesDto.SpeciesSummaryDto;
import dnd.manager.app.mapper.SpeciesMapper;
import dnd.manager.app.model.SpeciesEntities.Species;
import static dnd.manager.app.repository.SpeciesRepositories.spec.SpeciesSpecifications.*;

@Service
public class SpeciesService {

    private final SpeciesRepository repository;
    private final SpeciesMapper mapper;

    public SpeciesService(SpeciesRepository repository, SpeciesMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public SpeciesResponseDto findById(Long id){
        return mapper.toResponseDto(repository.findById(id).orElse(null));
    }

    public Page<SpeciesSummaryDto> findSpecies(
        String name,
        String size,
        Integer walkSpeed,
        Pageable pageable
    ){
        Specification<Species> spec = Specification
        .where(hasName(name))
        .and(hasSize(size))
        .and(hasWalkSpeed(walkSpeed));        
        
        Page<Species> species = repository.findAll(spec, pageable);
        
        return species.map(mapper::toSummaryDto);
    }

}