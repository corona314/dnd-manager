package dnd.manager.app.model.SubclassEntities;

import java.util.List;

import dnd.manager.app.model.ClassEntities.ClassEntity;
import dnd.manager.app.model.ClassEntities.SpellcastingType;
import dnd.manager.app.model.FeatureEntities.Feature;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "subclass")
@Getter
@Setter
@NoArgsConstructor

public class Subclass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "class_id")
    private ClassEntity classEntity;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "spellcasting_type", nullable = false)
    private SpellcastingType spellcastingType;

    @ManyToMany
    @JoinTable(
        name = "subclass_feature",
        joinColumns = @JoinColumn(name = "subclass_id"),
        inverseJoinColumns = @JoinColumn(name = "feature_id")
    )
    private List<Feature> features;

    @OneToMany(mappedBy = "subclass")
    private List<SubclassFeature> subclassFeatures;

    @OneToMany(mappedBy = "subclass")
    private List<SubclassSpell> spells;
}