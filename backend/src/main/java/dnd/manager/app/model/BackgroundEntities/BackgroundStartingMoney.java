package dnd.manager.app.model.BackgroundEntities;

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
@Table(name = "background_starting_money")
@Getter
@Setter
@NoArgsConstructor
@IdClass(BackgroundStartingMoneyId.class)
public class BackgroundStartingMoney {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "background_id")
    private Background background;

    @Id
    @Column(name = "option_group")
    private String optionGroup;

    @Column(name = "amount", nullable = false)
    private Integer amount;
}