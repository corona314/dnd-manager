package dnd.manager.app.DTO;

public class SpecieCatalogDTO {
    private Long id;
    private String name;
    private int size;
    private int base_speed;
    private String description;

    public SpecieCatalogDTO(Long id, String name, int size, int base_speed, String description) {
        this.id = id;
        this.name = name;
        this.size = size;
        this.base_speed = base_speed;
        this.description = description;
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

    public int getBase_speed() {
        return base_speed;
    }

    public void setBase_speed(int base_speed) {
        this.base_speed = base_speed;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
