package dnd.manager.app.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "armor")
public class Armor {

    @Id
    private Integer objectId;

    private Integer acBase;
    private Integer acMax;
    private Integer strMin;
    private Boolean stealthDis;

    @OneToOne
    @MapsId
    @JoinColumn(name = "object_id")
    private ObjectEntity object;
}