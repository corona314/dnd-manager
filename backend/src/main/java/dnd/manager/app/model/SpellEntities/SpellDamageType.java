package dnd.manager.app.model.SpellEntities;

import dnd.manager.app.model.DamageType;
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
@Table(name = "spell_damage_type")
@Getter
@Setter
@NoArgsConstructor
@IdClass(SpellDamageTypeId.class)

public class SpellDamageType {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "spell_id", nullable = false)
    private Spell spell;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "damage_type_id", nullable = false)
    private DamageType damageType;    

    @Column(name = "always", nullable = false)
    private Boolean always;
}
