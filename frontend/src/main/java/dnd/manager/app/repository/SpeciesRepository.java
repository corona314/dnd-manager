package dnd.manager.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dnd.manager.app.model.Species;

public interface SpeciesRepository extends JpaRepository<Species, Long> {


}