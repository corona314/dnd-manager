package dnd.manager.app.repository.ItemRepositories;

import org.springframework.data.jpa.repository.JpaRepository;

import dnd.manager.app.model.ItemEntities.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {

}
