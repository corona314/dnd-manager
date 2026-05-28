package dnd.manager.app.model.SubclassEntities;

import dnd.manager.app.model.FeatureEntities.Feature;
import jakarta.persistence.Column;
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
@Table(name = "subclass_feature")
@Getter
@Setter
@NoArgsConstructor

@IdClass(SubclassFeatureId.class)
public class SubclassFeature {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subclass_id", nullable = false)
    private Subclass subclass;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feature_id", nullable = false)
    private Feature feature;

    @Column(name = "level", nullable = false)
    private Integer level;

}