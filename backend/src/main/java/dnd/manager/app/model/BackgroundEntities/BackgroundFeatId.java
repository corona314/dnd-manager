package dnd.manager.app.model.BackgroundEntities;

import java.io.Serializable;
import java.util.Objects;

public class BackgroundFeatId implements Serializable {

    private Long background;
    private Long feat;

    public BackgroundFeatId() {}

    public BackgroundFeatId(Long background, Long feat) {
        this.background = background;
        this.feat = feat;
    }

    public Long getBackground() { return background; }
    public void setBackground(Long background) { this.background = background; }

    public Long getFeat() { return feat; }
    public void setFeat(Long feat) { this.feat = feat; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BackgroundFeatId)) return false;
        BackgroundFeatId that = (BackgroundFeatId) o;
        return Objects.equals(background, that.background) && Objects.equals(feat, that.feat);
    }

    @Override
    public int hashCode() {
        return Objects.hash(background, feat);
    }
}