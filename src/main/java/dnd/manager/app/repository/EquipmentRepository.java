package dnd.manager.app.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import dnd.manager.app.model.Equipment;
public interface EquipmentRepository extends JpaRepository<Equipment, Long>  {

}
