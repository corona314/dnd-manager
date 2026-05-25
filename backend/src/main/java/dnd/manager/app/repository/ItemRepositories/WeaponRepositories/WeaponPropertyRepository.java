package dnd.manager.app.repository.ItemRepositories.WeaponRepositories;

import org.springframework.data.jpa.repository.JpaRepository;

import dnd.manager.app.model.ItemEntities.WeaponEntities.WeaponProperty;

public interface WeaponPropertyRepository extends JpaRepository<WeaponProperty, Long> {

}
