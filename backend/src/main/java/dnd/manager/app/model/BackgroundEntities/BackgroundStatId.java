package dnd.manager.app.model.BackgroundEntities;

import java.io.Serializable;
import java.util.Objects;

public class BackgroundStatId implements Serializable {

    private Long background;
    private Long stat;

    public BackgroundStatId() {}

    public BackgroundStatId(Long background, Long stat) {
        this.background = background;
        this.stat = stat;
    }

    public Long getBackground() { return background; }
    public void setBackground(Long background) { this.background = background; }

    public Long getStat() { return stat; }
    public void setStat(Long stat) { this.stat = stat; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BackgroundStatId)) return false;
        BackgroundStatId that = (BackgroundStatId) o;
        return Objects.equals(background, that.background) && Objects.equals(stat, that.stat);
    }

    @Override
    public int hashCode() {
        return Objects.hash(background, stat);
    }
}