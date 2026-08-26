package dnd.manager.app.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import dnd.manager.app.dto.CharacterDto.CharacterCreateDto;
import dnd.manager.app.dto.CharacterDto.CharacterFeatResponseDto;
import dnd.manager.app.dto.CharacterDto.CharacterItemResponseDto;
import dnd.manager.app.dto.CharacterDto.CharacterResourceDto;
import dnd.manager.app.dto.CharacterDto.CharacterResponseDto;
import dnd.manager.app.dto.CharacterDto.CharacterSkillResponseDto;
import dnd.manager.app.dto.CharacterDto.CharacterAbilityDto;
import dnd.manager.app.dto.CharacterDto.CharacterClassResponseDto;
import dnd.manager.app.dto.CharacterDto.CharacterSpellResponseDto;
import dnd.manager.app.dto.CharacterDto.CharacterSpellSlotResponseDto;
import dnd.manager.app.dto.CharacterDto.CharacterSummaryDto;
import dnd.manager.app.model.CharacterEntities.CharacterEntity;
import dnd.manager.app.model.CharacterEntities.CharacterFeat;
import dnd.manager.app.model.CharacterEntities.CharacterItem;
import dnd.manager.app.model.CharacterEntities.CharacterResource;
import dnd.manager.app.model.CharacterEntities.CharacterSkill;
import dnd.manager.app.model.CharacterEntities.CharacterSpell;
import dnd.manager.app.model.CharacterEntities.CharacterSpellSlot;
import dnd.manager.app.model.CharacterEntities.CharacterStatus;
import dnd.manager.app.model.CharacterEntities.CharacterAbility;
import dnd.manager.app.model.CharacterEntities.CharacterClass;

@Component
public class CharacterMapper {

    private final SpeciesMapper speciesMapper;
    private final ClassMapper classMapper;
    private final BackgroundMapper backgroundMapper;
    private final FeatureMapper featureMapper;
    private final ItemMapper itemMapper;
    private final SkillMapper skillMapper;
    private final SpellMapper spellMapper;
    private final FeatMapper featMapper;

    public CharacterMapper(SpeciesMapper speciesMapper, ClassMapper classMapper, BackgroundMapper backgroundMapper, ItemMapper itemMapper, SkillMapper skillMapper, SpellMapper spellMapper, FeatMapper featMapper, FeatureMapper featureMapper) {
        this.speciesMapper = speciesMapper;
        this.classMapper = classMapper;
        this.backgroundMapper = backgroundMapper;
        this.featureMapper = featureMapper;
        this.itemMapper = itemMapper;
        this.skillMapper = skillMapper;
        this.spellMapper = spellMapper;
        this.featMapper = featMapper;
    }

    public CharacterSummaryDto toSummaryDto(CharacterEntity e) {
        return new CharacterSummaryDto(
            e.getId(),
            e.getName(),
            e.getLevel(),
            e.getClasses() != null ? e.getClasses().stream().map(c -> c.getClassEntity().getName()).toList() : List.of(),
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
            e.getBackground() == null ? null : backgroundMapper.toSummaryDto(e.getBackground()),
            e.getClasses() != null ? e.getClasses().stream().map(this::toClassResponseDto).toList() : List.of(),
            e.getAbilities() != null ? e.getAbilities().stream().map(this::toStatDto).toList() : List.of(),
            e.getSkills() != null ? e.getSkills().stream().map(this::toSkillResponseDto).toList() : List.of(),
            e.getFeatures() != null ? e.getFeatures().stream().map(cf -> featureMapper.toDto(cf.getFeature())).toList() : List.of(),
            e.getItems() != null ? e.getItems().stream().map(this::toItemResponseDto).toList() : List.of(),
            e.getSpells() != null ? e.getSpells().stream().map(this::toSpellResponseDto).toList() : List.of(),
            e.getFeats() != null ? e.getFeats().stream().map(this::toFeatResponseDto).toList() : List.of(),
            e.getResources() != null ? e.getResources().stream().map(this::toResourceDto).toList() : List.of(),
            e.getSpellSlots() != null ? e.getSpellSlots().stream().map(this::toSpellSlotResponseDto).toList() : List.of(),
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

    private CharacterFeatResponseDto toFeatResponseDto(CharacterFeat cf) {
        return new CharacterFeatResponseDto(
            featMapper.toDto(cf.getFeat()),
            cf.getSource(),
            cf.getSourceLevel()
        );
    }

    private CharacterResourceDto toResourceDto(CharacterResource resource) {
        return new CharacterResourceDto(
            resource.getName(),
            resource.getClassEntity() != null ? resource.getClassEntity().getName() : null,
            resource.getCurrentValue(),
            resource.getMaxValue()
        );
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

    private CharacterClassResponseDto toClassResponseDto(CharacterClass characterClass) {
        return new CharacterClassResponseDto(
            classMapper.toSummaryDto(characterClass.getClassEntity()),
            characterClass.getSubclass() != null ? classMapper.toSubclassSummaryDto(characterClass.getSubclass()) : null,
            characterClass.getLevel()
        );
    }

    private CharacterSpellSlotResponseDto toSpellSlotResponseDto(CharacterSpellSlot spellSlot) {
        return new CharacterSpellSlotResponseDto(
            spellSlot.getSpellLevel(),
            spellSlot.getCurrentSlots(),
            spellSlot.getMaxSlots()
        );
    }
}