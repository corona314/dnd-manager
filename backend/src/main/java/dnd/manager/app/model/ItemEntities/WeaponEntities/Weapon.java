package dnd.manager.app.model.ItemEntities.WeaponEntities;

import java.util.List;

import dnd.manager.app.model.ItemEntities.Item;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToMany;
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
    private Long id;

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
    @JoinColumn(name = "id")
    private Item item;

    @OneToMany(mappedBy = "weapon", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WeaponWeaponProperty> properties;


    @OneToMany(mappedBy = "weapon", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WeaponDamage> weaponDamages;
}
