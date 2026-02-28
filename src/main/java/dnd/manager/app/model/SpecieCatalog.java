package dnd.manager.app.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor


@Entity
@Table(name="specie")
public class SpecieCatalog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int size;
    @Column(name = "walk_speed")
    private int walkSpeed;
    private String description;
    @Column(name = "fly_speed")
    private int flySpeed;
    
    public int getWalkSpeed() {
        return walkSpeed;
    }
    public void setWalkSpeed(int walkSpeed) {
        this.walkSpeed = walkSpeed;
    }
    public int getFlySpeed() {
        return flySpeed;
    }
    public void setFlySpeed(int flySpeed) {
        this.flySpeed = flySpeed;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getSize() {
        return size;
    }
    public void setSize(int size) {
        this.size = size;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Specie [id=" + id + ", name=" + name + ", size=" + size + ", walkSpeed=" + walkSpeed + ", flySpeed=" + flySpeed +
        ", description=" + description.substring(0, 30) + "...]";
    }


}
