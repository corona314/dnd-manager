package dnd.manager.app.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="character_equipment")
public class Equipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long character_id;
    private Long equipment_id;
    private boolean equipped;
    private int quantity;

    @Override
    public String toString() {
        return "Equipment [id=" + id + ", character_id=" + character_id + ", equipped=" + equipped + ", equipment_id=" + equipment_id + ", quantity=" + quantity + "]";
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

    public Long getEquipment_id() {
        return equipment_id;
    }

    public void setEquipment_id(Long equipment_id) {
        this.equipment_id = equipment_id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

}
