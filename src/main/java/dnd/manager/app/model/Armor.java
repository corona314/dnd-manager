package dnd.manager.app.model;

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
@Table(name = "armor")
@Getter
@Setter
@NoArgsConstructor
public class Armor {

    @Id
    private Long objectId;
    @Column(name = "ac_base", nullable = false)
    private Integer acBase;
    @Column(name = "ac_max", nullable = false)
    private Integer acMax;
    @Column(name = "str_min")
    private Integer strMin;
    @Column(name = "stealth_dis", nullable = false)
    private Boolean stealthDis;
    
    @ManyToOne
    @JoinColumn(name = "armor_type_id")
    private ArmorType armorType;

    @OneToOne
    @MapsId
    @JoinColumn(name = "object_id")
    private ObjectEntity object;
}