package dnd.manager.app.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "specie")
@Getter
@Setter
@NoArgsConstructor
public class Specie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "size")
    private String size;

    @Column(name = "walk_speed", nullable = false)
    private Integer walkSpeed;
    
    @Column(name = "description")
    private String description;

    @Column(name = "fly_speed")
    private Integer flySpeed;

    

}
