package dnd.manager.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dnd.manager.app.model.ObjectEntity;

public interface ObjectRepository extends JpaRepository<ObjectEntity, Long> {

}
