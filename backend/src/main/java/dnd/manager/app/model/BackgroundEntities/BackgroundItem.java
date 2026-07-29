package dnd.manager.app.model.BackgroundEntities;


import dnd.manager.app.model.ItemEntities.Item;
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
@Table(name = "background_item")
@Getter
@Setter
@NoArgsConstructor
@IdClass(BackgroundItemId.class)
public class BackgroundItem {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "background_id")
    private Background background;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @Id
    @Column(name = "option_group")
    private String optionGroup;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "optional", nullable = false)
    private Boolean optional;
}
