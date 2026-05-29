package dnd.manager.app.model.CharacterEntities;

import dnd.manager.app.model.Skill;
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
@Table(name = "character_item")
@Getter
@Setter
@NoArgsConstructor
@IdClass(CharacterItemId.class)

public class CharacterItem {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "character_id", nullable = false)
    private CharacterEntity character;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Skill item;    

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "equipped", nullable = false)
    private Boolean equipped;

    @Column(name = "attuned", nullable = false)
    private Boolean attuned;

}
