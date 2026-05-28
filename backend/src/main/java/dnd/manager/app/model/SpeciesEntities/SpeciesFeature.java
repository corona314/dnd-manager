package dnd.manager.app.model.SpeciesEntities;

import dnd.manager.app.model.FeatureEntities.Feature;
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
@Table(name = "species_feature")
@Getter
@Setter
@NoArgsConstructor

@IdClass(SpeciesFeatureId.class)
public class SpeciesFeature {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "species_id", nullable = false)
    private Species species;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feature_id", nullable = false)
    private Feature feature;

}