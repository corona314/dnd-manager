package dnd.manager.app.model.BonusEntities;

import dnd.manager.app.model.ItemEntities.Item;
import dnd.manager.app.model.TraitEntities.Trait;
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
@Table(name = "bonus_trait_item")
@Getter
@Setter
@NoArgsConstructor
@IdClass(BonusTraitObjectId.class)
public class BonusTraitObject {

    @Id
    @Column(name = "trait_id")
    private Long traitId;

    @Id
    @Column(name = "item_id")
    private Long objectId;

    @Column(name = "value", nullable = false)
    private Integer value;

    @ManyToOne
    @JoinColumn(name = "trait_id", insertable = false, updatable = false)
    private Trait trait;

    @ManyToOne
    @JoinColumn(name = "item_id", insertable = false, updatable = false)
    private Item item;
}