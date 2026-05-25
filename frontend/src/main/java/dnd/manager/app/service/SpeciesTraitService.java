package dnd.manager.app.service;

import dnd.manager.app.model.SpeciesTrait;
import dnd.manager.app.model.SpeciesTraitId;
import dnd.manager.app.repository.SpeciesTraitRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SpeciesTraitService {

    private final SpeciesTraitRepository speciesTraitRepository;

    public SpeciesTraitService(SpeciesTraitRepository speciesTraitRepository) {
        this.speciesTraitRepository = speciesTraitRepository;
    }

    public List<SpeciesTrait> findAll() {
        return speciesTraitRepository.findAll();
    }

    public Optional<SpeciesTrait> findById(SpeciesTraitId id) {
        return speciesTraitRepository.findById(id);
    }

    public List<SpeciesTrait> findBySpeciesId(Long speciesId) {
        return speciesTraitRepository.findBySpeciesId(speciesId);
    }

    public SpeciesTrait save(SpeciesTrait speciesTrait) {
        return speciesTraitRepository.save(speciesTrait);
    }

    public void deleteById(SpeciesTraitId id) {
        speciesTraitRepository.deleteById(id);
    }
}