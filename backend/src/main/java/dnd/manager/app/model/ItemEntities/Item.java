package dnd.manager.app.model.ItemEntities;

import java.util.List;

import dnd.manager.app.model.ItemEntities.ArmorEntities.Armor;
import dnd.manager.app.model.ItemEntities.ShieldEntities.Shield;
import dnd.manager.app.model.ItemEntities.WeaponEntities.Weapon;
import dnd.manager.app.model.FeatureEntities.Feature;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "item")
@Getter
@Setter
@NoArgsConstructor

public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true, length = 120)
    private String name;
        
    @Column(name = "weight")
    private Float weight;
    
    @Column(name = "price")
    private Integer price;

    @ManyToOne
    @JoinColumn(name = "item_type_id")
    private ItemType itemType;

    @Column(name = "magic")
    private Boolean magic;

    @Column(name = "attunement")
    private Boolean attunement;

    @Column(name = "rarity", length = 20)
    private String rarity;

    @Column(name = "description")
    private String description;

    @OneToOne(mappedBy = "item")
    private Armor armor;

    @OneToOne(mappedBy = "item")
    private Weapon weapon;

    @OneToOne(mappedBy = "item")
    private Shield shield;


    @ManyToMany
    @JoinTable(
        name = "item_feature",
        joinColumns = @JoinColumn(name = "item_id"),
        inverseJoinColumns = @JoinColumn(name = "feature_id")
    )
    private List<Feature> features;

}
