package dnd.manager.app.model;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "character")
public class CharacterEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String name;
    private Integer level;
    private Integer maxHp;

    @ManyToOne
    @JoinColumn(name = "specie_id")
    private Specie specie;

    @ManyToOne
    @JoinColumn(name = "class_id")
    private ClassEntity classEntity;

    @ManyToOne
    @JoinColumn(name = "subclass_id")
    private Subclass subclass;

    private Integer currentHp;
    private Integer speed;
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "character")
    private List<CharacterSkill> skills;

    @OneToMany(mappedBy = "character")
    private List<CharacterStat> stats;

    @OneToMany(mappedBy = "character")
    private List<CharacterResource> resources;
}