package dnd.manager.app.repository.ItemRepositories;

import org.springframework.data.jpa.repository.JpaRepository;

import dnd.manager.app.model.ItemEntities.ItemType;

public interface ItemTypeRepository extends JpaRepository<ItemType, Long> {

}
