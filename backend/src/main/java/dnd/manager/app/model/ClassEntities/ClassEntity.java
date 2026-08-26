package dnd.manager.app.model.ClassEntities;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "class")
@Getter
@Setter
@NoArgsConstructor
public class ClassEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "hit_point_die", nullable = false)
    private String hitPointDie;

    @Column(name = "shield", nullable = false)
    private Boolean shield;

    @Column(name = "number_skills", nullable = false)
    private Integer numberSkills;

    @Column(name = "number_tools", nullable = false)
    private Integer numberTools;

    @Enumerated(EnumType.STRING)
    @Column(name = "spellcasting_type", nullable = false)
    private SpellcastingType spellcastingType;

    @OneToMany(mappedBy = "classEntity")
    private List<ClassSavingThrow> savingThrows;

    @OneToMany(mappedBy = "classEntity")
    private List<ClassFeature> features;

    @OneToMany(mappedBy = "classEntity")
    private List<ClassSpell> spells;

    @OneToMany(mappedBy = "classEntity")
    private List<ClassWeapon> weapons;

    @OneToMany(mappedBy = "classEntity")
    private List<ClassArmorType> armorTypes;

    @OneToMany(mappedBy = "classEntity")
    private List<ClassSkill> skills;

    @OneToMany(mappedBy = "classEntity", fetch = FetchType.LAZY)
    private List<ClassItem> items;

    @OneToMany(mappedBy = "classEntity", fetch = FetchType.LAZY)
    private List<ClassStartingMoney> startingMoney;

    @OneToMany(mappedBy = "classEntity")
    private List<ClassResource> resources;
}
