package dnd.manager.app.model.SubclassEntities;

import dnd.manager.app.model.SpellEntities.Spell;
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
@Table(name = "subclass_spell")
@Getter
@Setter
@NoArgsConstructor

@IdClass(SubclassSpellId.class)
public class SubclassSpell {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subclass_id", nullable = false)
    private Subclass subclass;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "spell_id", nullable = false)
    private Spell spell;

}
