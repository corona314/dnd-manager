package dnd.manager.app.service;

import dnd.manager.app.model.SpecieTrait;
import dnd.manager.app.model.SpecieTraitId;
import dnd.manager.app.repository.SpecieTraitRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SpecieTraitService {

    private final SpecieTraitRepository specieTraitRepository;

    public SpecieTraitService(SpecieTraitRepository specieTraitRepository) {
        this.specieTraitRepository = specieTraitRepository;
    }

    public List<SpecieTrait> findAll() {
        return specieTraitRepository.findAll();
    }

    public Optional<SpecieTrait> findById(SpecieTraitId id) {
        return specieTraitRepository.findById(id);
    }

    public List<SpecieTrait> findBySpecieId(Long specieId) {
        return specieTraitRepository.findBySpecieId(specieId);
    }

    public SpecieTrait save(SpecieTrait specieTrait) {
        return specieTraitRepository.save(specieTrait);
    }

    public void deleteById(SpecieTraitId id) {
        specieTraitRepository.deleteById(id);
    }
}