package dnd.manager.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import dnd.manager.app.model.Subclass;

public interface SubclassRepository extends JpaRepository<Subclass, Long> {

    List<Subclass> findByClassEntityId(Long classId);

}
