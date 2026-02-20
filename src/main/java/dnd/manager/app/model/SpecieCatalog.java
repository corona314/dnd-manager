package dnd.manager.app.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data

@Entity
@Table(name="species_catalog")
public class SpecieCatalog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int size;
    private int base_speed;
    private String description;
    
    @Override
    public String toString() {
        return "Specie [id=" + id + ", name=" + name + ", size=" + size + ", base_speed=" + base_speed + 
        ", description=" + description.substring(0, 30) + "...]";
    }
}
