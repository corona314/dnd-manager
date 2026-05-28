package dnd.manager.app.model.BonusEntities;

import dnd.manager.app.model.Stat;
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
@Table(name = "bonus_feature_stat")
@Getter
@Setter
@NoArgsConstructor
@IdClass(BonusFeatureStatId.class)
public class BonusFeatureStat {

    @Id
    @Column(name = "feature_id")
    private Long featureId;

    @Id
    @Column(name = "stat_id")
    private Long statId;

    @Column(name = "value", nullable = false)
    private Integer value;

    @ManyToOne
    @JoinColumn(name = "feature_id", insertable = false, updatable = false)
    private Feature feature;

    @ManyToOne
    @JoinColumn(name = "stat_id", insertable = false, updatable = false)
    private Stat stat;
}