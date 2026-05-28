package dnd.manager.app.service.FeatureServices;

import java.util.List;

import org.springframework.stereotype.Service;

import dnd.manager.app.model.FeatureEntities.Feature;
import dnd.manager.app.repository.FeatureRepositories.FeatureRepository;

@Service
public class FeatureService {

    private final FeatureRepository featureRepository;

    public FeatureService(FeatureRepository featureRepository) {
        this.featureRepository = featureRepository;
    }

    public List<Feature> findAll() {
        return featureRepository.findAll();
    }

    public Feature findById(Long id) {
        return featureRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Feature not found with id: " + id));
    }

    public Feature save(Feature feature) {
        return featureRepository.save(feature);
    }

    public void deleteById(Long id) {
        featureRepository.deleteById(id);
    }

    // Todos los features de un tipo concreto (racial, class, feat...)
    public List<Feature> findByType(Long featureTypeId) {
        return featureRepository.findByFeatureTypeId(featureTypeId);
    }
}