package dnd.manager.app.model.BonusEntities;

import dnd.manager.app.model.Feat;
import dnd.manager.app.model.Ability;
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
/*
    Para dotes que dan bonificadores a las abilities (+1 a STR o CON)
*/
@Entity
@Table(name = "bonus_feat_ability")
@Getter
@Setter
@NoArgsConstructor

@IdClass(BonusFeatAbilityId.class)
public class BonusFeatAbility {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feat_id", nullable = false)
    private Feat feat;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ability_id", nullable = false)
    private Ability ability;

    @Column(name = "value", nullable = false)
    private Integer value = 1;

}