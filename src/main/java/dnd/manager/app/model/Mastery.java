package dnd.manager.app.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "mastery")
public class Mastery {

    @Id
    private Long id;

    private String name;
    private String description;
}
