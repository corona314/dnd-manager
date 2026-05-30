package dnd.manager.app.model.BackgroundEntities;

import java.io.Serializable;
import java.util.Objects;

public class BackgroundToolId implements Serializable {

    private Long background;
    private Long item;

    public BackgroundToolId() {}

    public BackgroundToolId(Long background, Long item) {
        this.background = background;
        this.item = item;
    }

    public Long getBackground() { return background; }
    public void setBackground(Long background) { this.background = background; }

    public Long getItem() { return item; }
    public void setItem(Long item) { this.item = item; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BackgroundToolId)) return false;
        BackgroundToolId that = (BackgroundToolId) o;
        return Objects.equals(background, that.background) && Objects.equals(item, that.item);
    }

    @Override
    public int hashCode() {
        return Objects.hash(background, item);
    }
}