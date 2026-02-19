package dnd.manager.app.model;

import java.sql.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data

@Entity
@Table(name="characters")
public class Characters {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long user_id;
    private int max_hp;
    private int current_hp;
    private int specie_id;
    private int class_id;
    private int subclass_id;
    private Date created_at;
    private String name;
    private int level;
    private int speed;
    
    @Override
    public String toString() {
        return "Characters [id=" + id + "user_id="+ user_id + ", name=" + name + ", specie=" + specie_id + ", class_id=" + class_id + 
        ", subclass_id=" + subclass_id + ", level=" + level + ", max_hp=" + max_hp + ", current_hp=" + current_hp + 
        ", speed=" + speed + ", created_at=" + created_at.toString() +"]";
    }
    
    @PrePersist
    protected void onCreate() {
        this.created_at = new Date(System.currentTimeMillis());
    }
}

