package dnd.manager.app.model.BackgroundEntities;

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
@Table(name = "background_feature")
@Getter
@Setter
@NoArgsConstructor
@IdClass(BackgroundFeatureId.class)
public class BackgroundFeature {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "background_id", nullable = false)
    private Background background;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feature_id", nullable = false)
    private Feature feature;

}