package dnd.manager.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dnd.manager.app.model.Trait;

public interface TraitRepository extends JpaRepository<Trait, Long> {

}
