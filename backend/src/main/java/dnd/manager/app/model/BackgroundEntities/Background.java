package dnd.manager.app.model.BackgroundEntities;

import java.util.List;

import dnd.manager.app.model.ItemEntities.Item;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "background")
@Getter
@Setter
@NoArgsConstructor
public class Background {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true, length = 60)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "number_tools", nullable = false)
    private Integer numberTools;

    @OneToMany(mappedBy = "background", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BackgroundSkill> backgroundSkills;

    @OneToMany(mappedBy = "background", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BackgroundAbility> backgroundAbilities;

    @OneToMany(mappedBy = "background", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BackgroundFeat> backgroundFeats;

    @ManyToMany
    @JoinTable(
        name = "background_tool",
        joinColumns = @JoinColumn(name = "background_id"),
        inverseJoinColumns = @JoinColumn(name = "item_id")
    )
    private List<Item> tools;

    @OneToMany(mappedBy = "background", fetch = FetchType.LAZY)
    private List<BackgroundItem> items;

    @OneToMany(mappedBy = "background", fetch = FetchType.LAZY)
    private List<BackgroundStartingMoney> startingMoney;


}