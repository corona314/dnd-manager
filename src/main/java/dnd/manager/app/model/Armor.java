package dnd.manager.app.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "armor")
public class Armor {

    @Id
    private Long objectId;

    private Integer acBase;
    private Integer acMax;
    private Integer strMin;
    private Boolean stealthDis;
    
    @ManyToOne
    @JoinColumn(name = "armor_type_id")
    private ArmorType armorType;

    @OneToOne
    @MapsId
    @JoinColumn(name = "object_id")
    private ObjectEntity object;
}