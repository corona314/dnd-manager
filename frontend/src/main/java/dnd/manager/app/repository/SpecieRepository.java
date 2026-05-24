package dnd.manager.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dnd.manager.app.model.Specie;

public interface SpecieRepository extends JpaRepository<Specie, Long> {


}