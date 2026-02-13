package dnd.manager.app.model;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Characters {
    
    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private String race;
    private String characterClass;
    private String characterSubclass;
    private int level;

    @Embedded
    private Stats stats;
    
    
    public Characters() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRace() { return race; }
    public void setRace(String race) { this.race = race; }

    public String getCharacterClass() { return characterClass; }
    public void setCharacterClass(String characterClass) { this.characterClass = characterClass; }

    public String getCharacterSubclass() { return characterSubclass; }
    public void setCharacterSubclass(String characterSubclass) { this.characterSubclass = characterSubclass; }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    public Stats getStats() { return stats; }
    public void setStats(Stats stats) { this.stats = stats; }
}
