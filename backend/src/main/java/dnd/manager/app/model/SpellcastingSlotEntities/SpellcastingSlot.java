package dnd.manager.app.model.SpellcastingSlotEntities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "spellcasting_slot")
@Getter
@Setter
@NoArgsConstructor

@IdClass(SpellcastingSlotId.class)
public class SpellcastingSlot {

    @Id
    @JoinColumn(name = "caster_level", nullable = false)
    private Integer casterLevel;

    @Id
    @JoinColumn(name = "spell_level", nullable = false)
    private Integer spellLevel;

    @Column(name = "slots", nullable = false)
    private Integer slots;

}
