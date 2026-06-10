package dnd.manager.app.model.ItemEntities.ShieldEntities;

import dnd.manager.app.model.ItemEntities.Item;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "shield")
@Getter
@Setter
@NoArgsConstructor
public class Shield {

    @Id
    private Long id;

    @Column(name = "ac_bonus", nullable = false)
    private Integer acBonus;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private Item item;
}
