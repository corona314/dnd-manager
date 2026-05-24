package dnd.manager.app.model;

import java.time.LocalDateTime;
import java.util.List;

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
@Table(name = "character")
@Getter
@Setter
@NoArgsConstructor
public class CharacterEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "level", nullable = false)
    private Integer level;

    @Column(name = "max_hp", nullable = false)
    private Integer maxHp;

    @ManyToOne
    @JoinColumn(name = "specie_id", nullable = false)
    private Specie specie;

    @ManyToOne
    @JoinColumn(name = "class_id", nullable = false)
    private ClassEntity classEntity;

    @ManyToOne
    @JoinColumn(name = "subclass_id")
    private Subclass subclass;

    @Column(name = "current_hp", nullable = false)
    private Integer currentHp;

    @Column(name = "walk_speed", nullable = false)
    private Integer walkSpeed;

    @Column(name = "fly_speed", nullable = false)
    private Integer flySpeed;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "character")
    private List<CharacterSkill> skills;

    @OneToMany(mappedBy = "character")
    private List<CharacterStat> stats;

    @OneToMany(mappedBy = "character")
    private List<CharacterResource> resources;

    @ManyToOne
    @JoinColumn(name = "background_id")
    private Background background;

    @OneToMany(mappedBy = "character", fetch = FetchType.LAZY)
    private List<CharacterFeat> feats;

    @OneToMany(mappedBy = "character", fetch = FetchType.LAZY)
    private List<CharacterSpell> spells;

    @OneToMany(mappedBy = "character", fetch = FetchType.LAZY)
    private List<CharacterSavingThrow> savingThrows;
}