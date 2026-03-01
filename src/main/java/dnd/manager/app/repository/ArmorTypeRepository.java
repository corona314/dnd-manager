package dnd.manager.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import dnd.manager.app.model.ArmorType;

public interface ArmorTypeRepository extends JpaRepository<ArmorType, Long> {

}
