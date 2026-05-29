package dnd.manager.app.model.ItemEntities.WeaponEntities;

import java.util.List;

import dnd.manager.app.model.DamageType;
import dnd.manager.app.model.ItemEntities.Item;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "weapon")
@Getter
@Setter
@NoArgsConstructor

public class Weapon {

    @Id
    private Long itemId;

    @Column(name = "damage_dice", nullable = false)
    private String damageDice;

    @ManyToOne
    @JoinColumn(name = "damage_type_id", nullable = false)
    private DamageType damageType;

    @ManyToOne
    @JoinColumn(name = "mastery_id", nullable = false)
    private Mastery mastery;

    @Column(name = "range_normal")
    private Integer rangeNormal;

    @Column(name = "range_long")
    private Integer rangeLong;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "weapon_category", nullable = false)
    private WeaponCategory weaponCategory;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "weapon_type", nullable = false)
    private WeaponType weaponType;

    @OneToOne
    @MapsId
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToMany
    @JoinTable(
        name = "weapon_weapon_property",
        joinColumns = @JoinColumn(name = "weapon_id"),
        inverseJoinColumns = @JoinColumn(name = "property_id")
    )
    private List<WeaponProperty> properties;
}
