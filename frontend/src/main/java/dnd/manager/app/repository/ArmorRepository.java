package dnd.manager.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import dnd.manager.app.model.Armor;

public interface ArmorRepository extends JpaRepository<Armor, Long> {


    List<Armor> findByArmorTypeId(Long armorTypeId);
}
