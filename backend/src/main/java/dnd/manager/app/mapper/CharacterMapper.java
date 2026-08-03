package dnd.manager.app.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import dnd.manager.app.dto.CharacterDto.CharacterCreateDto;
import dnd.manager.app.dto.CharacterDto.CharacterItemResponseDto;
import dnd.manager.app.dto.CharacterDto.CharacterResponseDto;
import dnd.manager.app.dto.CharacterDto.CharacterSkillResponseDto;
import dnd.manager.app.dto.CharacterDto.CharacterAbilityDto;
import dnd.manager.app.dto.CharacterDto.CharacterSpellResponseDto;
import dnd.manager.app.dto.CharacterDto.CharacterSummaryDto;
import dnd.manager.app.model.CharacterEntities.CharacterEntity;
import dnd.manager.app.model.CharacterEntities.CharacterItem;
import dnd.manager.app.model.CharacterEntities.CharacterSkill;
import dnd.manager.app.model.CharacterEntities.CharacterSpell;
import dnd.manager.app.model.CharacterEntities.CharacterStatus;
import dnd.manager.app.model.CharacterEntities.CharacterAbility;

@Component
public class CharacterMapper {

    private final SpeciesMapper speciesMapper;
    private final ClassMapper classMapper;
    private final BackgroundMapper backgroundMapper;
    private final ItemMapper itemMapper;
    private final SkillMapper skillMapper;
    private final SpellMapper spellMapper;

    public CharacterMapper(SpeciesMapper speciesMapper, ClassMapper classMapper, BackgroundMapper backgroundMapper, ItemMapper itemMapper, SkillMapper skillMapper, SpellMapper spellMapper) {
        this.speciesMapper = speciesMapper;
        this.classMapper = classMapper;
        this.backgroundMapper = backgroundMapper;
        this.itemMapper = itemMapper;
        this.skillMapper = skillMapper;
        this.spellMapper = spellMapper;
    }

    public CharacterSummaryDto toSummaryDto(CharacterEntity e) {
        return new CharacterSummaryDto(
            e.getId(),
            e.getName(),
            e.getLevel(),
            e.getClassEntity() != null ? e.getClassEntity().getName() : null,
            e.getSpecies() != null ? e.getSpecies().getName() : null,
            e.getStatus(),
            e.getUpdatedAt()
        );
    }

    public CharacterResponseDto toResponseDto(CharacterEntity e) {
        return new CharacterResponseDto(
            e.getName(),
            e.getLevel(),
            e.getCurrentHp(),
            e.getMaxHp(),
            e.getMoney(),
            e.getExperience(),
            e.getWalkSpeed(),
            e.getFlySpeed(),
            e.getSpecies() == null ? null : speciesMapper.toSummaryDto(e.getSpecies()),
            e.getClassEntity() == null ? null : classMapper.toSummaryDto(e.getClassEntity()),
            e.getSubclass() == null ? null : classMapper.toSubclassSummaryDto(e.getSubclass()),
            e.getBackground() == null ? null : backgroundMapper.toSummaryDto(e.getBackground()),
            e.getAbilities() != null ? e.getAbilities().stream().map(this::toStatDto).toList() : List.of(),
            e.getSkills() != null ? e.getSkills().stream().map(this::toSkillResponseDto).toList() : List.of(),
            e.getItems() != null ? e.getItems().stream().map(this::toItemResponseDto).toList() : List.of(),
            e.getSpells() != null ? e.getSpells().stream().map(this::toSpellResponseDto).toList() : List.of(),
            e.getStatus(),
            e.getCreatedAt(),
            e.getUpdatedAt(),
            e.getFinalizedAt()
        );
    }

    public CharacterEntity toEntity(CharacterCreateDto dto) {
    CharacterEntity entity = new CharacterEntity();
    entity.setName(dto.name());
    entity.setStatus(CharacterStatus.DRAFT);
    return entity;

    }

    private CharacterAbilityDto toStatDto(CharacterAbility ability) {
        return new CharacterAbilityDto(
            ability.getAbility().getCode(),
            ability.getBaseValue()
        );
    }

    private CharacterSkillResponseDto toSkillResponseDto(CharacterSkill skill) {
        return new CharacterSkillResponseDto(
            skillMapper.toDto(skill.getSkill()),
            skill.getProficiency(),
            skill.getExpertise()
        );
    }

    private CharacterItemResponseDto toItemResponseDto(CharacterItem item){
        return new CharacterItemResponseDto(
            itemMapper.toSummaryDto(item.getItem()),
            item.getQuantity(), 
            item.getEquipped(), 
            item.getAttuned()
        );
    }

    private CharacterSpellResponseDto toSpellResponseDto(CharacterSpell spell){
        return new CharacterSpellResponseDto(
            spellMapper.toSummaryDto(spell.getSpell()),
            spell.getPrepared(),
            spell.getAlwaysPrepared()
        );
    }
}