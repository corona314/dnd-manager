package dnd.manager.app.model;

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
@Table(name = "feat_stat_bonus")
@Getter
@Setter
@NoArgsConstructor

@IdClass(FeatStatBonusId.class)
public class FeatStatBonus {

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