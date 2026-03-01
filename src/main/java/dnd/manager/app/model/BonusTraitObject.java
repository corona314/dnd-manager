package dnd.manager.app.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

@Entity
@Table(name = "bonus_trait_object")
@IdClass(BonusTraitObjectId.class)
public class BonusTraitObject {

    @Id
    private Long traitId;

    @Id
    private Long objectId;

    private Integer value;
}
