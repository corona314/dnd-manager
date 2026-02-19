package dnd.manager.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dnd.manager.app.model.SpecieCatalog;

public interface SpecieCatalogRepository extends JpaRepository<SpecieCatalog, Long> {

}
