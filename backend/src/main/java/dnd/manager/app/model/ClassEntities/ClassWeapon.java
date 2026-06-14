package dnd.manager.app.model.ClassEntities;

import dnd.manager.app.model.ItemEntities.WeaponEntities.WeaponCategory;
import dnd.manager.app.model.ItemEntities.WeaponEntities.WeaponProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "class_weapon")
@Getter
@Setter
@NoArgsConstructor
@IdClass(ClassWeaponId.class)
public class ClassWeapon {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", insertable = false, updatable = false)
    private ClassEntity classEntity;
    
    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "weapon_category", insertable = false, updatable = false)
    private WeaponCategory weaponCategory;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "required_weapon_property_id")
    private WeaponProperty requiredWeaponProperty;

}
