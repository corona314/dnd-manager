package dnd.manager.app.DTO;

public class SpecieCatalogDTO {
    private Long id;
    private String name;
    private int size;
    private int walkSpeed;
    private String description;
    private int flySpeed;

    public SpecieCatalogDTO(Long id, String name, int size, int walkSpeed, String description, int flySpeed) {
        this.id = id;
        this.name = name;
        this.size = size;
        this.walkSpeed = walkSpeed;
        this.description = description;
        this.flySpeed = flySpeed;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getWalkSpeed() {
        return walkSpeed;
    }

    public void setWalkSpeed(int base_speed) {
        this.walkSpeed = base_speed;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getFlySpeed() {
        return flySpeed;
    }

    public void setFlySpeed(int flySpeed) {
        this.flySpeed = flySpeed;
    }
}
