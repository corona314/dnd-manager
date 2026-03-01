package dnd.manager.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dnd.manager.app.model.WeaponProperty;

public interface WeaponPropertyRepository extends JpaRepository<WeaponProperty, Long> {

}
