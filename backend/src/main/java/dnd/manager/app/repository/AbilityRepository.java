package dnd.manager.app.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import dnd.manager.app.model.Ability;

public interface AbilityRepository extends JpaRepository<Ability, Long> {

    Optional<Ability> findByCode(String code);

}
