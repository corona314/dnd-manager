package dnd.manager.app.model;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="characters")
public class Characters {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String race;
    private String charclass;
    private String charsubclass;
    private int level;
    private int maxhealth;
    @Embedded
    private Stats stats;
    
    @Override
    public String toString() {
        return "Characters [id=" + id + ", name=" + name + ", race=" + race + ", charclass=" + charclass + 
        ", charsubclass=" + charsubclass + ", level=" + level + ", maxhealth=" + maxhealth + ", " + stats.toString() + "]";
    }
}

