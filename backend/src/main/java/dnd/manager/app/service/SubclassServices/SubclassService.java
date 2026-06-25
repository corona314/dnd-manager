package dnd.manager.app.service.SubclassServices;

import java.util.List;

import org.springframework.stereotype.Service;

import dnd.manager.app.dto.ClassDto.SubclassResponseDto;
import dnd.manager.app.dto.ClassDto.SubclassSummaryDto;
import dnd.manager.app.mapper.ClassMapper;
import dnd.manager.app.model.SubclassEntities.Subclass;
import dnd.manager.app.repository.SubclassRepositories.SubclassRepository;
import dnd.manager.app.repository.SubclassRepositories.SubclassFeatureRepository;

@Service
public class SubclassService {

    private final SubclassRepository subclassRepository;
    private final SubclassFeatureRepository subclassFeatureRepository;
    private final ClassMapper classMapper;

    public SubclassService(SubclassRepository subclassRepository,
                           SubclassFeatureRepository subclassFeatureRepository,
                           ClassMapper classMapper) {
        this.subclassRepository = subclassRepository;
        this.subclassFeatureRepository = subclassFeatureRepository;
        this.classMapper = classMapper;
    }

    public List<Subclass> findAll() {
        return subclassRepository.findAll();
    }

    public Subclass findById(Long id) {
        return subclassRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subclass not found with id: " + id));
    }

    public SubclassResponseDto findByIdDto(Long id) {
        return classMapper.subclassToResponseDto(findById(id));
    }

    // Subclases de una clase concreta — útil para el select del front
    public List<Subclass> findByClass(Long classId) {
        return subclassRepository.findByClassEntityId(classId);
    }

    public List<SubclassSummaryDto> findByClassDto(Long classId) {
        return findByClass(classId).stream()
                .map(classMapper::subclassToSummaryDto)
                .toList();
    }

    public Subclass save(Subclass subclass) {
        return subclassRepository.save(subclass);
    }

    public void deleteById(Long id) {
        subclassRepository.deleteById(id);
    }

    // Features de subclase filtrados por nivel del personaje
    public List<?> findFeaturesBySubclassAndLevel(Long subclassId, Integer level) {
        return subclassFeatureRepository.findBySubclassIdAndLevelLessThanEqual(subclassId, level);
    }
}