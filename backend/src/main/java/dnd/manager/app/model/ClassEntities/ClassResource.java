package dnd.manager.app.model.ClassEntities;

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
@Table(name = "class_resource")
@Getter
@Setter
@NoArgsConstructor
@IdClass(ClassResourceId.class)
public class ClassResource{

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", insertable = false, updatable = false)
    private ClassEntity classEntity;
    
    @Id
    @JoinColumn(name = "name", nullable = false)
    private String name;
    
    @Id
    @Column(name = "level", nullable = false)
    private Integer level;

    @Column(name = "value", nullable = false)
    private Integer value;

}
