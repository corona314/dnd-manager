package dnd.manager.app.service.FeatureServices;

import java.util.List;

import org.springframework.stereotype.Service;

import dnd.manager.app.model.FeatureEntities.FeatureType;
import dnd.manager.app.repository.FeatureRepositories.FeatureTypeRepository;

@Service
class FeatureTypeService {

    private final FeatureTypeRepository featureTypeRepository;

    FeatureTypeService(FeatureTypeRepository featureTypeRepository) {
        this.featureTypeRepository = featureTypeRepository;
    }

    public List<FeatureType> findAll() {
        return featureTypeRepository.findAll();
    }

    public FeatureType findById(Long id) {
        return featureTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("FeatureType not found with id: " + id));
    }

    public FeatureType findByName(String name) {
        return featureTypeRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("FeatureType not found: " + name));
    }

    public FeatureType save(FeatureType featureType) {
        return featureTypeRepository.save(featureType);
    }
}