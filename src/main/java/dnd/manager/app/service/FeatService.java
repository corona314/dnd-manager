package dnd.manager.app.service;

import java.util.List;

import org.springframework.stereotype.Service;

import dnd.manager.app.model.Feat;
import dnd.manager.app.repository.FeatRepository;

@Service
public class FeatService {

    private final FeatRepository featRepository;

    public FeatService(FeatRepository featRepository) {
        this.featRepository = featRepository;
    }

    public List<Feat> findAll() {
        return featRepository.findAll();
    }

    public Feat findById(Long id) {
        return featRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Feat not found with id: " + id));
    }

    public Feat save(Feat feat) {
        return featRepository.save(feat);
    }

    public void deleteById(Long id) {
        featRepository.deleteById(id);
    }

    // Feats de origen — son los que se otorgan al crear personaje desde background
    public List<Feat> findOriginFeats() {
        return featRepository.findByFeatCategory("origin");
    }

    // Feats generales — los que se eligen al subir de nivel
    public List<Feat> findGeneralFeats() {
        return featRepository.findByFeatCategory("general");
    }

    // Fighting styles (en 2024 son feats)
    public List<Feat> findFightingStyles() {
        return featRepository.findByFeatCategory("fighting_style");
    }

    // Epic Boons (nivel 19+)
    public List<Feat> findEpicBoons() {
        return featRepository.findByFeatCategory("epic_boon");
    }

    // Feats disponibles para un personaje según su nivel
    // Los epic boons requieren nivel 19+
    public List<Feat> findAvailableFeats(Integer characterLevel) {
        if (characterLevel >= 19) {
            return featRepository.findAll();
        }
        return featRepository.findAll().stream()
                .filter(f -> !"epic_boon".equals(f.getFeatCategory()))
                .toList();
    }
}