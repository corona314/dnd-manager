package dnd.manager.app.model.BackgroundEntities;

import java.io.Serializable;
import java.util.Objects;

public class BackgroundAbilityId implements Serializable {

    private Long background;
    private Long ability;

    public BackgroundAbilityId() {}

    public BackgroundAbilityId(Long background, Long ability) {
        this.background = background;
        this.ability = ability;
    }

    public Long getBackground() { return background; }
    public void setBackground(Long background) { this.background = background; }

    public Long getAbility() { return ability; }
    public void setStat(Long ability) { this.ability = ability; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BackgroundAbilityId)) return false;
        BackgroundAbilityId that = (BackgroundAbilityId) o;
        return Objects.equals(background, that.background) && Objects.equals(ability, that.ability);
    }

    @Override
    public int hashCode() {
        return Objects.hash(background, ability);
    }
}