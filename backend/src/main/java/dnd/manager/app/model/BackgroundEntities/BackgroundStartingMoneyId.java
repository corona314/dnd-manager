package dnd.manager.app.model.BackgroundEntities;

import java.io.Serializable;
import java.util.Objects;

public class BackgroundStartingMoneyId implements Serializable {
    private Long background;
    private String optionGroup;

    public BackgroundStartingMoneyId() {}

    public Long getBackground() { return background; }
    public void setBackground(Long background) { this.background = background; }

    public String getOptionGroup() { return optionGroup; }
    public void setOptionGroup(String optionGroup) { this.optionGroup = optionGroup; }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BackgroundStartingMoneyId)) return false;
        BackgroundStartingMoneyId that = (BackgroundStartingMoneyId) o;
        return Objects.equals(background, that.background) &&
               Objects.equals(optionGroup, that.optionGroup);
    }

    @Override
    public int hashCode() {
        return Objects.hash(background, optionGroup);
    }
}