package dnd.manager.app.model.ItemEntities.WeaponEntities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "weapon_weapon_property")
@Getter
@Setter
@NoArgsConstructor
@IdClass(WeaponWeaponPropertyId.class)
public class WeaponWeaponProperty {
  
    @Id
    @ManyToOne
    @JoinColumn(name = "weapon_id")
    private Weapon weapon;
    
    @Id
    @ManyToOne
    @JoinColumn(name = "property_id", nullable = false)
    private WeaponProperty property;

    @Column(name = "value", nullable = false, length = 45)
    private String value;

}
