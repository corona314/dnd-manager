package dnd.manager.app.model.CharacterEntities;

import dnd.manager.app.model.FeatureEntities.Feature;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "character_spell_slot")
@Getter
@Setter
@NoArgsConstructor
@IdClass(CharacterSpellSlotId.class)

public class CharacterSpellSlot {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "character_id", nullable = false)
    private CharacterEntity character;

    @Id
    @Column(name = "spell_level", nullable = false)
    private Integer spellLevel;

    @Column(name = "current_slots", nullable = false)
    private Integer currentSlots;

    @Column(name = "max_slots", nullable = false)
    private Integer maxSlots;
}
