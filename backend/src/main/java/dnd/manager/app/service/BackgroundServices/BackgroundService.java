package dnd.manager.app.service.BackgroundServices;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import dnd.manager.app.dto.BackgroundDto.BackgroundResponseDto;
import dnd.manager.app.dto.BackgroundDto.BackgroundSummaryDto;
import dnd.manager.app.mapper.BackgroundMapper;
import dnd.manager.app.model.BackgroundEntities.Background;
import dnd.manager.app.repository.BackgroundRepositories.BackgroundRepository;
import static dnd.manager.app.repository.BackgroundRepositories.spec.BackgroundSpecifications.*;

@Service
public class BackgroundService {

    private final BackgroundRepository repository;
    private final BackgroundMapper mapper;

    public BackgroundService(BackgroundRepository repository, BackgroundMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public BackgroundResponseDto findById(Long id) {
        return mapper.toResponseDto(repository.findById(id).orElse(null));
    }

    public Page<BackgroundSummaryDto> findBackgrounds(
        String name,
        Pageable pageable
    ){
        Specification<Background> spec = Specification
        .where(hasName(name));
        
        Page<Background> backgrounds = repository.findAll(spec, pageable);
        
        return backgrounds.map(mapper::toSummaryDto);
    }

}