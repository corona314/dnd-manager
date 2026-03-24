package dnd.manager.app.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import dnd.manager.app.model.Stat;

public interface StatRepository extends JpaRepository<Stat, Long> {

    Optional<Stat> findByCode(String code);

}
