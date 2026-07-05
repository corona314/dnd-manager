package dnd.manager.app.mapper;

import org.springframework.stereotype.Component;

import dnd.manager.app.dto.BackgroundDto.BackgroundResponseDto;
import dnd.manager.app.dto.BackgroundDto.BackgroundSummaryDto;
import dnd.manager.app.model.Ability;
import dnd.manager.app.model.BackgroundEntities.Background;
import dnd.manager.app.model.BackgroundEntities.BackgroundAbility;
import dnd.manager.app.model.BackgroundEntities.BackgroundFeat;
import dnd.manager.app.model.BackgroundEntities.BackgroundSkill;

@Component
public class BackgroundMapper {


    private final ItemMapper itemMapper;
    private final SkillMapper skillMapper;
    private final FeatMapper featMapper;
    
    BackgroundMapper(ItemMapper itemMapper, SkillMapper skillMapper, FeatMapper featMapper) {
        this.itemMapper = itemMapper;
        this.skillMapper = skillMapper;
        this.featMapper = featMapper;
    }

    public BackgroundSummaryDto toSummaryDto(Background e) {
        return new BackgroundSummaryDto(
            e.getId(),
            e.getName(),
            e.getDescription()
        );
    }

    public BackgroundResponseDto toResponseDto(Background e) {
        return new BackgroundResponseDto(
            e.getName(),
            e.getDescription(),
            e.getBackgroundAbilities().stream().map(BackgroundAbility::getAbility).map(Ability::getCode).toList(),
            e.getBackgroundFeats().stream().map(BackgroundFeat::getFeat).map(featMapper::toDto).toList(),
            e.getBackgroundSkills().stream().map(BackgroundSkill::getSkill).map(skillMapper::toDto).toList(),
            e.getTools().stream().map(t -> itemMapper.toSummaryDto(t)).toList()
        );
    }


}
