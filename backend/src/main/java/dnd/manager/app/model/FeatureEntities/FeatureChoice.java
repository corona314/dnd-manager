package dnd.manager.app.model.FeatureEntities;

import jakarta.persistence.Column;
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
@Table(name = "feature_choice")
@Getter
@Setter
@NoArgsConstructor
@IdClass(FeatureChoiceId.class)
public class FeatureChoice {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feature_id", nullable = false)
    private Feature parentFeature;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "choice_id", nullable = false)
    private Feature choice;

    @Column(name = "level")
    private Integer level;

    @Column(name = "prerequisite")
    private String prerequisite;
}