package dnd.manager.app.service.SpeciesServices;

import java.util.List;

import org.springframework.stereotype.Service;

import dnd.manager.app.repository.SpeciesRepositories.SpeciesRepository;
import dnd.manager.app.repository.SpeciesRepositories.SpeciesFeatureRepository;
import dnd.manager.app.model.SpeciesEntities.Species;

@Service
public class SpeciesService {

    private final SpeciesRepository speciesRepository;
    private final SpeciesFeatureRepository speciesFeatureRepository;

    public SpeciesService(SpeciesRepository speciesRepository, SpeciesFeatureRepository speciesFeatureRepository) {
        this.speciesRepository = speciesRepository;
        this.speciesFeatureRepository = speciesFeatureRepository;
    }

    public List<Species> findAll() {
        return speciesRepository.findAll();
    }

    public Species findById(Long id) {
        return speciesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Species not found with id: " + id));
    }

    public Species save(Species species) {
        return speciesRepository.save(species);
    }

    public void deleteById(Long id) {
        speciesRepository.deleteById(id);
    }

    // Todas las especies que tengan velocidad de vuelo
    public List<Species> findFlying() {
        return speciesRepository.findAll().stream()
                .filter(s -> s.getFlySpeed() != null && s.getFlySpeed() > 0)
                .toList();
    }

    // Features de una especie concreta
    public List<?> findFeaturesBySpecie(Long speciesId) {
        return speciesFeatureRepository.findBySpeciesId(speciesId);
    }
}