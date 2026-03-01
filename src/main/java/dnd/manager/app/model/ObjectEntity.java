package dnd.manager.app.model;

import java.util.List;

import org.aspectj.apache.bcel.generic.ObjectType;

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

@Entity
@Table(name = "object")
public class ObjectEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Float weight;
    private Integer prize;

    @ManyToOne
    @JoinColumn(name = "object_type_id")
    private ObjectType objectType;

    private Boolean magic;

    @OneToOne(mappedBy = "object")
    private Armor armor;

    @OneToOne(mappedBy = "object")
    private Weapon weapon;

    @ManyToMany
    @JoinTable(
        name = "object_trait",
        joinColumns = @JoinColumn(name = "object_id"),
        inverseJoinColumns = @JoinColumn(name = "trait_id")
    )
    private List<Trait> traits;

}
