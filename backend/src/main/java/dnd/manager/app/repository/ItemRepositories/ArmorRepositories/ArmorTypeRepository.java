package dnd.manager.app.repository.ItemRepositories.ArmorRepositories;

import org.springframework.data.jpa.repository.JpaRepository;

import dnd.manager.app.model.ItemEntities.ArmorEntities.ArmorType;

public interface ArmorTypeRepository extends JpaRepository<ArmorType, Long> {

}
