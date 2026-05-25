package dnd.manager.app.repository.ItemRepositories.ArmorRepositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import dnd.manager.app.model.ItemEntities.ArmorEntities.Armor;

public interface ArmorRepository extends JpaRepository<Armor, Long> {


    List<Armor> findByArmorTypeId(Long armorTypeId);
}
