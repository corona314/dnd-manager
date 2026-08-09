package dnd.manager.app.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import dnd.manager.app.dto.FeatDto;
import dnd.manager.app.mapper.FeatMapper;
import dnd.manager.app.model.Feat;
import dnd.manager.app.repository.FeatRepositories.FeatRepository;
import dnd.manager.app.repository.FeatRepositories.spec.FeatSpecifications;

@Service
public class FeatService {

    private final FeatRepository featRepository;
    private final FeatMapper featMapper;

    public FeatService(FeatRepository featRepository, FeatMapper featMapper) {
        this.featRepository = featRepository;
        this.featMapper = featMapper;
    }

    public List<Feat> findAll() {
        return featRepository.findAll();
    }

    public FeatDto findById(Long id) {
        return featRepository.findById(id)
                .map(featMapper::toDto)
                .orElse(null);
    }


    public Page<FeatDto> findFeats(String name, String repeatable, Pageable pageable) {
        Specification<Feat> spec = Specification
            .where(FeatSpecifications.hasName(name))
            .and(FeatSpecifications.hasRepeatable(repeatable));

        return featRepository.findAll(spec, pageable).map(featMapper::toDto);
    }
}