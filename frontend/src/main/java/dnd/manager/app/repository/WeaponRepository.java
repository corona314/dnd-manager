package dnd.manager.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dnd.manager.app.model.Weapon;

public interface WeaponRepository extends JpaRepository<Weapon, Long> {

}
