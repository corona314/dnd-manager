package dnd.manager.app.model;

import java.util.List;

import dnd.manager.app.model.BonusEntities.BonusFeatAbility;
import dnd.manager.app.model.CharacterEntities.CharacterFeat;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "feat")
@Getter
@Setter
@NoArgsConstructor

public class Feat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true, length = 80)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "prerequisite", length = 255)
    private String prerequisite;

    @Column(name = "repeatable", nullable = false)
    private Boolean repeatable = false;

    // 'origin', 'general', 'fighting_style', 'epic_boon'
    @Column(name = "feat_category", length = 30)
    private String featCategory;

    @OneToMany(mappedBy = "feat", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BonusFeatAbility> abilityBonuses;

    @OneToMany(mappedBy = "feat", fetch = FetchType.LAZY)
    private List<CharacterFeat> characterFeats;

}