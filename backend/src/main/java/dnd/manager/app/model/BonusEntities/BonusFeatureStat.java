package dnd.manager.app.model.BonusEntities;

import dnd.manager.app.model.Ability;
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

/*
    Para rasgos que dan bonificadores a las abilities (+1 a STR)
*/
@Entity
@Table(name = "bonus_feature_ability")
@Getter
@Setter
@NoArgsConstructor
@IdClass(BonusFeatureAbilityId.class)
public class BonusFeatureStat {

    @Id
    @Column(name = "feature_id")
    private Long featureId;

    @Id
    @Column(name = "ability_id")
    private Long abilityId;

    @Column(name = "value", nullable = false)
    private Integer value;

    @ManyToOne
    @JoinColumn(name = "feature_id", insertable = false, updatable = false)
    private Feature feature;

    @ManyToOne
    @JoinColumn(name = "ability_id", insertable = false, updatable = false)
    private Ability ability;
}