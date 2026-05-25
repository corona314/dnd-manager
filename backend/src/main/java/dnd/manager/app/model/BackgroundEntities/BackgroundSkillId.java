package dnd.manager.app.model.BackgroundEntities;

import java.io.Serializable;
import java.util.Objects;

public class BackgroundSkillId implements Serializable {

    private Long background;
    private Long skill;

    public BackgroundSkillId() {}

    public BackgroundSkillId(Long background, Long skill) {
        this.background = background;
        this.skill = skill;
    }

    public Long getBackground() { return background; }
    public void setBackground(Long background) { this.background = background; }

    public Long getSkill() { return skill; }
    public void setSkill(Long skill) { this.skill = skill; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BackgroundSkillId)) return false;
        BackgroundSkillId that = (BackgroundSkillId) o;
        return Objects.equals(background, that.background) && Objects.equals(skill, that.skill);
    }

    @Override
    public int hashCode() {
        return Objects.hash(background, skill);
    }
}