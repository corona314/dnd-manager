package dnd.manager.app.service.ClassServices;

import java.util.List;

import org.springframework.stereotype.Service;

import dnd.manager.app.model.ClassEntities.ClassEntity;
import dnd.manager.app.model.SubclassEntities.Subclass;
import dnd.manager.app.repository.ClassRepositories.ClassRepository;
import dnd.manager.app.repository.ClassRepositories.ClassSavingThrowRepository;
import dnd.manager.app.repository.ClassRepositories.ClassFeatureRepository;
import dnd.manager.app.repository.SubclassRepositories.SubclassRepository;

@Service
public class ClassService {

    private final ClassRepository classRepository;
    private final SubclassRepository subclassRepository;
    private final ClassFeatureRepository classFeatureRepository;
    private final ClassSavingThrowRepository classSavingThrowRepository;

    public ClassService(ClassRepository classRepository,
                        SubclassRepository subclassRepository,
                        ClassFeatureRepository classFeatureRepository,
                        ClassSavingThrowRepository classSavingThrowRepository) {
        this.classRepository = classRepository;
        this.subclassRepository = subclassRepository;
        this.classFeatureRepository = classFeatureRepository;
        this.classSavingThrowRepository = classSavingThrowRepository;
    }

    public List<ClassEntity> findAll() {
        return classRepository.findAll();
    }

    public ClassEntity findById(Long id) {
        return classRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Class not found with id: " + id));
    }

    public ClassEntity save(ClassEntity classEntity) {
        return classRepository.save(classEntity);
    }

    public void deleteById(Long id) {
        classRepository.deleteById(id);
    }

    // Subclases disponibles para una clase
    public List<Subclass> findSubclassesByClass(Long classId) {
        return subclassRepository.findByClassEntityId(classId);
    }

    // Features que otorga una clase, filtrados por nivel
    public List<?> findFeaturesByClassAndLevel(Long classId, Integer level) {
        return classFeatureRepository.findByClassEntityIdAndLevelLessThanEqual(classId, level);
    }

    // Saving throws de una clase
    public List<?> findSavingThrowsByClass(Long classId) {
        return classSavingThrowRepository.findByClassEntityId(classId);
    }
}