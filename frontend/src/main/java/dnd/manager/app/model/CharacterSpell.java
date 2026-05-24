package dnd.manager.app.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "character_spell",
    uniqueConstraints = @UniqueConstraint(columnNames = {"character_id", "spell_id"})
)
@Getter
@Setter
@NoArgsConstructor
public class CharacterSpell {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "character_id", nullable = false)
    private CharacterEntity character;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "spell_id", nullable = false)
    private Spell spell;

    // false = conocido, true = preparado activamente
    @Column(name = "prepared", nullable = false)
    private Boolean prepared = false;

    // true = siempre preparado por subclase/dominio/pacto
    @Column(name = "always_prepared", nullable = false)
    private Boolean alwaysPrepared = false;

}