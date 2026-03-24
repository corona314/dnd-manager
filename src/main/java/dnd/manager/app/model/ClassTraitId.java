package dnd.manager.app.model;

import java.io.Serializable;

public class ClassTraitId implements Serializable {
    private Long classId;
    private Long traitId;

    public ClassTraitId() {}

    public ClassTraitId(Long classId, Long traitId) {
        this.classId = classId;
        this.traitId = traitId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClassTraitId)) return false;

        ClassTraitId that = (ClassTraitId) o;

        if (!classId.equals(that.classId)) return false;
        return traitId.equals(that.traitId);
    }


}
