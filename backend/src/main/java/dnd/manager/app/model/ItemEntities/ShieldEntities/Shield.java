package dnd.manager.app.model.ItemEntities.ShieldEntities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "armor_type")
@Getter
@Setter
@NoArgsConstructor
public class Shield {

    @Id
    private Long itemId;

    @Column(name = "ac_bonus", nullable = false)
    private String acBonus;
}
