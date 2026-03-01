package dnd.manager.app.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "object_type")
public class ObjectType {

    @Id
    private Long id;
    private String name;
}