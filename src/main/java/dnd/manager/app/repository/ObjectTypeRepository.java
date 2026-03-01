package dnd.manager.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dnd.manager.app.model.ObjectType;

public interface ObjectTypeRepository extends JpaRepository<ObjectType, Long> {

}
