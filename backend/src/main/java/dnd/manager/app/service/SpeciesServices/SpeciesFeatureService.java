package dnd.manager.app.service.SpeciesServices;

import dnd.manager.app.model.SpeciesEntities.SpeciesFeature;
import dnd.manager.app.model.SpeciesEntities.SpeciesFeatureId;
import dnd.manager.app.repository.SpeciesRepositories.SpeciesFeatureRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SpeciesFeatureService {

    private final SpeciesFeatureRepository speciesFeatureRepository;

    public SpeciesFeatureService(SpeciesFeatureRepository speciesFeatureRepository) {
        this.speciesFeatureRepository = speciesFeatureRepository;
    }

    public List<SpeciesFeature> findAll() {
        return speciesFeatureRepository.findAll();
    }

    public Optional<SpeciesFeature> findById(SpeciesFeatureId id) {
        return speciesFeatureRepository.findById(id);
    }

    public List<SpeciesFeature> findBySpeciesId(Long speciesId) {
        return speciesFeatureRepository.findBySpeciesId(speciesId);
    }

    public SpeciesFeature save(SpeciesFeature speciesFeature) {
        return speciesFeatureRepository.save(speciesFeature);
    }

    public void deleteById(SpeciesFeatureId id) {
        speciesFeatureRepository.deleteById(id);
    }
}