package dnd.manager.app.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "subclass_trait")
@Getter
@Setter
@NoArgsConstructor

@IdClass(SubclassTraitId.class)
public class SubclassTrait {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subclass_id", nullable = false)
    private Subclass subclass;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trait_id", nullable = false)
    private Trait trait;

}