package dnd.manager.app.repository.SpellcastingSlotRepositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import dnd.manager.app.model.SpellcastingSlotEntities.SpellcastingSlot;
import dnd.manager.app.model.SpellcastingSlotEntities.SpellcastingSlotId;

public interface SpellcastingSlotRepository extends JpaRepository<SpellcastingSlot, SpellcastingSlotId> {

    List<SpellcastingSlot> findByCasterLevel(Integer casterLevel);
}
