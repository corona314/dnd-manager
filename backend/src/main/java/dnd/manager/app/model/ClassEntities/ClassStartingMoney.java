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
@Table(name = "class_starting_money")
@Getter
@Setter
@NoArgsConstructor
@IdClass(ClassStartingMoneyId.class)
public class ClassStartingMoney {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", insertable = false, updatable = false)
    private ClassEntity classEntity;
    
    @Id
    @JoinColumn(name = "option_group", insertable = false, updatable = false)
    private String optionGroup;
    
    @Column(name = "amount", nullable = false)
    private Integer amount;
}
