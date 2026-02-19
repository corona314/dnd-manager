package dnd.manager.app.DTO;

import java.sql.Date;

public class CharactersDTO {

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
    
    public CharactersDTO(Long id, Long user_id, int max_hp, int current_hp, int specie_id, int class_id,
            int subclass_id, Date created_at, String name, int level, int speed) {
        this.id = id;
        this.user_id = user_id;
        this.max_hp = max_hp;
        this.current_hp = current_hp;
        this.specie_id = specie_id;
        this.class_id = class_id;
        this.subclass_id = subclass_id;
        this.created_at = created_at;
        this.name = name;
        this.level = level;
        this.speed = speed;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public int getMax_hp() {
        return max_hp;
    }

    public void setMax_hp(int max_hp) {
        this.max_hp = max_hp;
    }

    public int getCurrent_hp() {
        return current_hp;
    }

    public void setCurrent_hp(int current_hp) {
        this.current_hp = current_hp;
    }

    public int getSpecie_id() {
        return specie_id;
    }

    public void setSpecie_id(int specie_id) {
        this.specie_id = specie_id;
    }

    public int getClass_id() {
        return class_id;
    }

    public void setClass_id(int class_id) {
        this.class_id = class_id;
    }

    public int getSubclass_id() {
        return subclass_id;
    }

    public void setSubclass_id(int subclass_id) {
        this.subclass_id = subclass_id;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    @Override
    public String toString() {
        return "CharactersDTO [id=" + id + "user_id="+ user_id + ", name=" + name + ", specie=" + specie_id + ", class_id=" + class_id + 
        ", subclass_id=" + subclass_id + ", level=" + level + ", max_hp=" + max_hp + ", current_hp=" + current_hp + 
        ", speed=" + speed + ", created_at=" + created_at.toString() +"]";
    }
    
}
