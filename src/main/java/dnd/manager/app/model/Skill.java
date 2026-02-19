package dnd.manager.app.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name="character_skill")
public class Skill {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long character_id;
    private Long skill_id;
    private boolean proficient;
    private boolean expertise;

    @Override
    public String toString() {
        return "Skills [id=" + id + ", character_id=" + character_id + ", skill_id=" + skill_id + ", proficient=" + proficient
                + ", expertise=" + expertise + "]";
    }

    
}
