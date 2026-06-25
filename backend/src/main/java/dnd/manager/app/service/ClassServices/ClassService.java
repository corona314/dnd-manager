package dnd.manager.app.service.ClassServices;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import dnd.manager.app.dto.ClassDto.ClassResponseDto;
import dnd.manager.app.dto.ClassDto.ClassSummaryDto;
import dnd.manager.app.dto.ClassDto.SubclassResponseDto;
import dnd.manager.app.dto.ClassDto.SubclassSummaryDto;
import dnd.manager.app.mapper.ClassMapper;
import dnd.manager.app.model.ClassEntities.ClassEntity;
import dnd.manager.app.repository.ClassRepositories.ClassRepository;
import dnd.manager.app.service.SubclassServices.SubclassService;
import static dnd.manager.app.repository.ClassRepositories.spec.ClassSpecifications.*;

import java.util.List;

@Service
public class ClassService {

    private final ClassRepository classRepository;
    private final ClassMapper mapper;
    private final SubclassService subclassService;

    public ClassService(ClassRepository classRepository, ClassMapper mapper, SubclassService subclassService) {
        this.classRepository = classRepository;
        this.mapper = mapper;
        this.subclassService = subclassService;
    }

    public ClassResponseDto findById(Long id) {
        return mapper.toResponseDto(classRepository.findById(id).orElse(null));
    }

    public Page<ClassSummaryDto> findClasses(
        String name,
        List<String> hitPointDie,
        int page,
        int size
    ){
        Specification<ClassEntity> spec = Specification
        .where(hasName(name))
        .and(hasHitPointDie(hitPointDie));        
        
        Page<ClassEntity> classes = classRepository.findAll(
                    spec,
                    PageRequest.of(page, size)
            );
        return classes.map(mapper::toSummaryDto);
    }

    public List<SubclassSummaryDto> findSubclassesByClass(Long classId) {
        return subclassService.findByClassDto(classId);
    }

    public SubclassResponseDto findSubclassById(Long subclassId) {
        return subclassService.findByIdDto(subclassId);
    }

}