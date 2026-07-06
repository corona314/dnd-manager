package dnd.manager.app.repository.SpeciesRepositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import dnd.manager.app.model.SpeciesEntities.Species;

public interface SpeciesRepository extends JpaRepository<Species, Long>, JpaSpecificationExecutor<Species> {


}