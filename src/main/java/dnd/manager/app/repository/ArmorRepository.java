package dnd.manager.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import dnd.manager.app.model.Armor;

public interface ArmorRepository extends JpaRepository<Armor, Long> {

}
