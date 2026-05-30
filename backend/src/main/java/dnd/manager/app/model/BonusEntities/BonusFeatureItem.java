package dnd.manager.app.model.BonusEntities;

import dnd.manager.app.model.ItemEntities.Item;
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
    Para armas o armaduras +1, +2, +3
*/
@Entity
@Table(name = "bonus_feature_item")
@Getter
@Setter
@NoArgsConstructor
@IdClass(BonusFeatureItemId.class)
public class BonusFeatureItem {

    @Id
    @Column(name = "feature_id")
    private Long featureId;

    @Id
    @Column(name = "item_id")
    private Long objectId;

    @Column(name = "value", nullable = false)
    private Integer value;

    @ManyToOne
    @JoinColumn(name = "feature_id", insertable = false, updatable = false)
    private Feature feature;

    @ManyToOne
    @JoinColumn(name = "item_id", insertable = false, updatable = false)
    private Item item;
}