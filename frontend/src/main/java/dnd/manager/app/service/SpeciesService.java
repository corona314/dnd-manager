package dnd.manager.app.service;

import java.util.List;

import org.springframework.stereotype.Service;

import dnd.manager.app.model.Species;
import dnd.manager.app.repository.SpeciesTraitRepository;
import dnd.manager.app.repository.SpeciesRepository;

@Service
public class SpeciesService {

    private final SpeciesRepository speciesRepository;
    private final SpeciesTraitRepository speciesTraitRepository;

    public SpecieService(SpecieRepository speciesRepository, SpeciesTraitRepository specieTraitRepository) {
        this.speciesRepository = speciesRepository;
        this.speciesTraitRepository = speciesTraitRepository;
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

    // Traits de una especie concreta
    public List<?> findTraitsBySpecie(Long speciesId) {
        return speciesTraitRepository.findBySpecieId(speciesId);
    }
}