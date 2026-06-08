package dnd.manager.app.model.ItemEntities.WeaponEntities;

import dnd.manager.app.model.DamageType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "weapon_damage")
@Getter
@Setter
@NoArgsConstructor

public class WeaponDamage {

    @Id
    private Long weaponId;

    @Column(name = "damage_roll", nullable = false)
    private String damageRoll;

    @ManyToOne
    @JoinColumn(name = "damage_type_id", nullable = false)
    private DamageType damageType;

    @OneToOne
    @MapsId
    @JoinColumn(name = "weapon_id")
    private Weapon weapon;

    @Column(name = "always", nullable = false)
    private String always;

}
