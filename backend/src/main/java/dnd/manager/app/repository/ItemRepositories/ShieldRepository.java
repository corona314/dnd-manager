package dnd.manager.app.repository.ItemRepositories;

import org.springframework.data.jpa.repository.JpaRepository;

import dnd.manager.app.model.ItemEntities.ShieldEntities.Shield;

public interface ShieldRepository extends JpaRepository<Shield, Long> {

}
