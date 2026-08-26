package dnd.manager.app.model.CharacterEntities;

import java.time.LocalDateTime;
import java.util.List;

import dnd.manager.app.model.User;
import dnd.manager.app.model.BackgroundEntities.Background;
import dnd.manager.app.model.SpeciesEntities.Species;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "`character`")
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

    @Column(name = "current_hp", nullable = false)
    private Integer currentHp;

    @Column(name = "max_hp", nullable = false)
    private Integer maxHp;

    @Column(name = "money", nullable = false)
    private Integer money;

    @Column(name = "experience", nullable = false)
    private Integer experience;

    @ManyToOne
    @JoinColumn(name = "species_id")
    private Species species;

    @Column(name = "walk_speed")
    private Integer walkSpeed;

    @Column(name = "fly_speed")
    private Integer flySpeed;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;    
    
    @Column(name = "finalized_at")
    private LocalDateTime finalizedAt;    
    
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private CharacterStatus status = CharacterStatus.DRAFT;

    @OneToMany(mappedBy = "character", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CharacterSkill> skills;

    @OneToMany(mappedBy = "character", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CharacterAbility> abilities;

    @OneToMany(mappedBy = "character", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CharacterResource> resources;

    @ManyToOne
    @JoinColumn(name = "background_id")
    private Background background;

    @OneToMany(mappedBy = "character", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CharacterItem> items;

    @OneToMany(mappedBy = "character", fetch = FetchType.LAZY)
    private List<CharacterFeat> feats;

    @OneToMany(mappedBy = "character", fetch = FetchType.LAZY)
    private List<CharacterSpell> spells;

    @OneToMany(mappedBy = "character", fetch = FetchType.LAZY)
    private List<CharacterSavingThrow> savingThrows;

    @OneToMany(mappedBy = "character", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CharacterFeature> features;

    @OneToMany(mappedBy = "character", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CharacterClass> classes;

    @OneToMany(mappedBy = "character", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CharacterSpellSlot> spellSlots;
}