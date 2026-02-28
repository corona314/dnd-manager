package dnd.manager.app.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

@Entity
@Table(name = "bonus_trait_stat")
@IdClass(BonusTraitStatId.class)
public class BonusTraitStat {

    @Id
    private Integer traitId;

    @Id
    private Integer statId;

    private Integer value;
}