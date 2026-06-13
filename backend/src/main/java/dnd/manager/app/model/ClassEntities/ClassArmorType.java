package dnd.manager.app.model.ClassEntities;

import dnd.manager.app.model.ItemEntities.ArmorEntities.ArmorType;
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
@Table(name = "class_armor_type")
@Getter
@Setter
@NoArgsConstructor
@IdClass(ClassArmorTypeId.class)
public class ClassArmorType {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", insertable = false, updatable = false)
    private ClassEntity classEntity;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "armor_id", insertable = false, updatable = false)
    private ArmorType armorType;

}
