package dnd.manager.app.service;

import java.util.List;

import org.springframework.stereotype.Service;

import dnd.manager.app.model.Specie;
import dnd.manager.app.repository.SpecieTraitRepository;
import dnd.manager.app.repository.SpecieRepository;

@Service
public class SpecieService {

    private final SpecieRepository specieRepository;
    private final SpecieTraitRepository specieTraitRepository;

    public SpecieService(SpecieRepository specieRepository, SpecieTraitRepository specieTraitRepository) {
        this.specieRepository = specieRepository;
        this.specieTraitRepository = specieTraitRepository;
    }

    public List<Specie> findAll() {
        return specieRepository.findAll();
    }

    public Specie findById(Long id) {
        return specieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Specie not found with id: " + id));
    }

    public Specie save(Specie specie) {
        return specieRepository.save(specie);
    }

    public void deleteById(Long id) {
        specieRepository.deleteById(id);
    }

    // Todas las especies que tengan velocidad de vuelo
    public List<Specie> findFlying() {
        return specieRepository.findAll().stream()
                .filter(s -> s.getFlySpeed() != null && s.getFlySpeed() > 0)
                .toList();
    }

    // Traits de una especie concreta
    public List<?> findTraitsBySpecie(Long specieId) {
        return specieTraitRepository.findBySpecieId(specieId);
    }
}