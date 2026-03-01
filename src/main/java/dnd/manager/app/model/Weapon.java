package dnd.manager.app.model;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "weapon")
public class Weapon {

    @Id
    private Long objectId;

    private String damageDice;

    @ManyToOne
    @JoinColumn(name = "damage_type_id")
    private DamageType damageType;

    @ManyToOne
    @JoinColumn(name = "mastery_id")
    private Mastery mastery;

    private Integer rangeNormal;
    private Integer rangeLong;

    @OneToOne
    @MapsId
    @JoinColumn(name = "object_id")
    private ObjectEntity object;

    @ManyToMany
    @JoinTable(
        name = "weapon_weapon_property",
        joinColumns = @JoinColumn(name = "weapon_id"),
        inverseJoinColumns = @JoinColumn(name = "property_id")
    )
    private List<WeaponProperty> properties;
}
