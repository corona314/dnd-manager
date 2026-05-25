package dnd.manager.app.model.BackgroundEntities;

import java.io.Serializable;
import java.util.Objects;

public class BackgroundTraitId implements Serializable {

    private Long background;
    private Long trait;

    public BackgroundTraitId() {}

    public BackgroundTraitId(Long background, Long trait) {
        this.background = background;
        this.trait = trait;
    }

    public Long getBackground() { return background; }
    public void setBackground(Long background) { this.background = background; }

    public Long getTrait() { return trait; }
    public void setTrait(Long trait) { this.trait = trait; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BackgroundTraitId)) return false;
        BackgroundTraitId that = (BackgroundTraitId) o;
        return Objects.equals(background, that.background) && Objects.equals(trait, that.trait);
    }

    @Override
    public int hashCode() {
        return Objects.hash(background, trait);
    }
}