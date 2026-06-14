package dnd.manager.app.model.ClassEntities;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

}
