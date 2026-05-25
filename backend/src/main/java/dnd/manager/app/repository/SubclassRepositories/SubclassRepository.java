package dnd.manager.app.repository.SubclassRepositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import dnd.manager.app.model.SubclassEntities.Subclass;

public interface SubclassRepository extends JpaRepository<Subclass, Long> {

    List<Subclass> findByClassEntityId(Long classId);

}
