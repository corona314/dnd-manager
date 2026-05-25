package dnd.manager.app.service.BackgroundServices;

import java.util.List;

import org.springframework.stereotype.Service;

import dnd.manager.app.model.BackgroundEntities.Background;
import dnd.manager.app.repository.BackgroundRepositories.BackgroundRepository;
import dnd.manager.app.repository.BackgroundRepositories.BackgroundSkillRepository;
import dnd.manager.app.repository.BackgroundRepositories.BackgroundTraitRepository;

@Service
public class BackgroundService {

    private final BackgroundRepository backgroundRepository;
    private final BackgroundSkillRepository backgroundSkillRepository;
    private final BackgroundTraitRepository backgroundTraitRepository;

    public BackgroundService(BackgroundRepository backgroundRepository,
                             BackgroundSkillRepository backgroundSkillRepository,
                             BackgroundTraitRepository backgroundTraitRepository) {
        this.backgroundRepository = backgroundRepository;
        this.backgroundSkillRepository = backgroundSkillRepository;
        this.backgroundTraitRepository = backgroundTraitRepository;
    }

    public List<Background> findAll() {
        return backgroundRepository.findAll();
    }

    public Background findById(Long id) {
        return backgroundRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Background not found with id: " + id));
    }

    public Background save(Background background) {
        return backgroundRepository.save(background);
    }

    public void deleteById(Long id) {
        backgroundRepository.deleteById(id);
    }

    // Skills de proficiencia que otorga este trasfondo
    public List<?> findSkillsByBackground(Long backgroundId) {
        return backgroundSkillRepository.findByBackgroundId(backgroundId);
    }

    // Traits/feats que otorga este trasfondo (incluye el Origin Feat)
    public List<?> findTraitsByBackground(Long backgroundId) {
        return backgroundTraitRepository.findByBackgroundId(backgroundId);
    }
}