package dnd.manager.app.model.SpellEntities;

import java.util.List;

import dnd.manager.app.model.DamageType;
import dnd.manager.app.model.Stat;
import dnd.manager.app.model.CharacterEntities.CharacterSpell;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "spell")
@Getter
@Setter
@NoArgsConstructor

public class Spell {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true, length = 80)
    private String name;

    // 0 = cantrip, 1-9 = spell level
    @Column(name = "level", nullable = false)
    private Integer level;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    private SpellSchool school;

    @Column(name = "casting_time", length = 60)
    private String castingTime;

    @Column(name = "range", length = 40)
    private String range;

    @Column(name = "duration", length = 60)
    private String duration;

    // 'V', 'V,S', 'V,S,M', etc.
    @Column(name = "components", length = 10)
    private String components;

    @Column(name = "material", columnDefinition = "TEXT")
    private String material;

    @Column(name = "concentration", nullable = false)
    private Boolean concentration = false;

    @Column(name = "ritual", nullable = false)
    private Boolean ritual = false;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "attack_roll")
    private Boolean attackRoll;
    
    @ManyToOne
    @JoinColumn(name = "saving_throw_stat_id")
    private Stat savingThrowStat;
    
    @Column(name = "damage_roll", length = 20)
    private String damageRoll;
    
    @ManyToOne
    @JoinColumn(name = "damage_type_id")    
    private DamageType damageType;

    @OneToMany(mappedBy = "spell", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CharacterSpell> characterSpells;

}