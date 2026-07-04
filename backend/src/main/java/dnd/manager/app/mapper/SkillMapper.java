package dnd.manager.app.mapper;


import org.springframework.stereotype.Component;

import dnd.manager.app.dto.SkillDto;
import dnd.manager.app.model.Skill;

@Component
public class SkillMapper {

    public SkillDto toDto(Skill s) {
        return new SkillDto(
            s.getName(),
            s.getAbility() == null ? null : s.getAbility().getCode()
        );
    }
    
}
