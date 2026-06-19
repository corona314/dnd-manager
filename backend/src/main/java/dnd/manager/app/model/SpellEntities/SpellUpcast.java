package dnd.manager.app.model.SpellEntities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "spell_upcast")
@Getter
@Setter
@NoArgsConstructor
@IdClass(SpellUpcastId.class)
public class SpellUpcast {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "spell_id", insertable = false, updatable = false)
    private Spell spell;

    @Id
    @Column(name = "level", nullable = false)
    private int level;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private UpcastType type;

    @Column(name = "damage_roll", length = 20)
    private String damageRoll;

    @Column(name = "description")
    private String description;

}