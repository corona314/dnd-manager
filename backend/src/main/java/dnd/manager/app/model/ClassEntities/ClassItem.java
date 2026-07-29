package dnd.manager.app.model.ClassEntities;

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
@Table(name = "class_item")
@Getter
@Setter
@NoArgsConstructor
@IdClass(ClassItemId.class)
public class ClassItem {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", insertable = false, updatable = false)
    private ClassEntity classEntity;
    
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", insertable = false, updatable = false)
    private Item item;
    
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "option_group", nullable = false)
    private String optionGroup;

    @Column(name = "optional", nullable = false)
    private Boolean optional;

}
