package dnd.manager.app.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="character_stat")
public class Stat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long character_id;
    private Long stat_id;
    private int base_value;

    @Override
    public String toString() {
        return "Stats [id=" + id + ", character_id=" + character_id + ", stat_id=" + stat_id + ", base_value=" + base_value + "]";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCharacter_id() {
        return character_id;
    }

    public void setCharacter_id(Long character_id) {
        this.character_id = character_id;
    }

    public Long getStat_id() {
        return stat_id;
    }

    public void setStat_id(Long stat_id) {
        this.stat_id = stat_id;
    }

    public int getBase_value() {
        return base_value;
    }

    public void setBase_value(int base_value) {
        this.base_value = base_value;
    }
}
