package dnd.manager.app.model.ClassEntities;

import dnd.manager.app.model.TraitEntities.Trait;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "class_trait")
@Getter
@Setter
@NoArgsConstructor
@IdClass(ClassTraitId.class)
public class ClassTrait {

    @Id
    @Column(name = "class_id")
    private Long classId;

    @Id
    @Column(name = "trait_id")
    private Long traitId;

    @Column(name = "level", nullable = false)
    private int level;

    @ManyToOne
    @JoinColumn(name = "class_id", insertable = false, updatable = false)
    private ClassEntity classEntity;

    @ManyToOne
    @JoinColumn(name = "trait_id", insertable = false, updatable = false)
    private Trait trait;

}
