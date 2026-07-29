package dnd.manager.app.model.BackgroundEntities;

import java.io.Serializable;
import java.util.Objects;

public class BackgroundItemId implements Serializable {
    private Long background;
    private Long item;
    private String optionGroup;

    public BackgroundItemId() {}

    public Long getBackground() { return background; }
    public void setBackground(Long background) { this.background = background; }

    public Long getItem() { return item; }
    public void setItem(Long item) { this.item = item; }

    public String getOptionGroup() { return optionGroup; }
    public void setOptionGroup(String optionGroup) { this.optionGroup = optionGroup; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BackgroundItemId)) return false;
        BackgroundItemId that = (BackgroundItemId) o;
        return Objects.equals(background, that.background) &&
               Objects.equals(item, that.item) &&
               Objects.equals(optionGroup, that.optionGroup);
    }

    @Override
    public int hashCode() {
        return Objects.hash(background, item, optionGroup);
    }
}