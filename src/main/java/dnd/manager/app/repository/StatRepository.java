package dnd.manager.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dnd.manager.app.model.Stat;

public interface StatRepository extends JpaRepository<Stat, Long> {

}
