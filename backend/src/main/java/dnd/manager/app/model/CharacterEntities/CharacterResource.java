package dnd.manager.app.model.CharacterEntities;

import dnd.manager.app.model.ClassEntities.ClassEntity;
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
@Table(name = "character_resource")
@Getter
@Setter
@NoArgsConstructor
@IdClass(CharacterResourceId.class)
public class CharacterResource {

    @Id
    @ManyToOne
    @JoinColumn(name = "character_id", nullable = false)
    private CharacterEntity character;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", nullable = false)
    private ClassEntity classEntity;

    @Id
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "current_value")
    private Integer currentValue;

    @Column(name = "max_value")
    private Integer maxValue;

    @Column(name = "text_value", length = 20)
    private String textValue;

    @Column(name = "increment")
    private Integer increment;
}