package dnd.manager.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import dnd.manager.app.model.DamageType;

public interface DamageTypeRepository extends JpaRepository<DamageType, Long> {

}
