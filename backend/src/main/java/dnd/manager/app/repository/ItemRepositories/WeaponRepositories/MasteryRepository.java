package dnd.manager.app.repository.ItemRepositories.WeaponRepositories;

import org.springframework.data.jpa.repository.JpaRepository;

import dnd.manager.app.model.ItemEntities.WeaponEntities.Mastery;

public interface MasteryRepository extends JpaRepository<Mastery, Long> {

}
