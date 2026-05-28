package dnd.manager.app.model.ClassEntities;

import dnd.manager.app.model.FeatureEntities.Feature;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "class_feature")
@Getter
@Setter
@NoArgsConstructor
@IdClass(ClassFeatureId.class)
public class ClassFeature {

    @Id
    @Column(name = "class_id")
    private Long classId;

    @Id
    @Column(name = "feature_id")
    private Long featureId;

    @Column(name = "level", nullable = false)
    private int level;

    @ManyToOne
    @JoinColumn(name = "class_id", insertable = false, updatable = false)
    private ClassEntity classEntity;

    @ManyToOne
    @JoinColumn(name = "feature_id", insertable = false, updatable = false)
    private Feature feature;

}
