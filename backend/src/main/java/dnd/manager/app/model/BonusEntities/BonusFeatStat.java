package dnd.manager.app.model.BonusEntities;

import dnd.manager.app.model.Feat;
import dnd.manager.app.model.Stat;
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
@Table(name = "bonus_feat_stat")
@Getter
@Setter
@NoArgsConstructor

@IdClass(BonusFeatStatId.class)
public class BonusFeatStat {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feat_id", nullable = false)
    private Feat feat;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stat_id", nullable = false)
    private Stat stat;

    @Column(name = "value", nullable = false)
    private Integer value = 1;

}