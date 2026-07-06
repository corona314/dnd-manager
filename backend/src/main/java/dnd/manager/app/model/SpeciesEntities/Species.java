package dnd.manager.app.model.SpeciesEntities;

import java.util.List;

import dnd.manager.app.model.FeatureEntities.Feature;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "species")
@Getter
@Setter
@NoArgsConstructor
public class Species {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "size")
    private String size;

    @Column(name = "walk_speed", nullable = false)
    private Integer walkSpeed;
    
    @Column(name = "description")
    private String description;

    @Column(name = "fly_speed")
    private Integer flySpeed;

    @ManyToMany
    @JoinTable(
        name = "species_feature",
        joinColumns = @JoinColumn(name = "species_id"),
        inverseJoinColumns = @JoinColumn(name = "feature_id")
    )
    private List<Feature> features;

}
