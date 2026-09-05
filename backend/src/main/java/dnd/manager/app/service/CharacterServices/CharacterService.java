package dnd.manager.app.service.CharacterServices;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import dnd.manager.app.dto.CharacterDto.CharacterCreateDto;
import dnd.manager.app.dto.CharacterDto.CharacterFeatDto;
import dnd.manager.app.dto.CharacterDto.CharacterItemDto;
import dnd.manager.app.dto.CharacterDto.CharacterPatchDto;
import dnd.manager.app.dto.CharacterDto.CharacterResponseDto;
import dnd.manager.app.dto.CharacterDto.CharacterSkillDto;
import dnd.manager.app.dto.CharacterDto.CharacterAbilityDto;
import dnd.manager.app.dto.CharacterDto.CharacterSpellDto;
import dnd.manager.app.dto.CharacterDto.CharacterSummaryDto;
import dnd.manager.app.mapper.CharacterMapper;
import dnd.manager.app.model.User;
import dnd.manager.app.model.CharacterEntities.CharacterEntity;
import dnd.manager.app.model.CharacterEntities.CharacterItem;
import dnd.manager.app.model.CharacterEntities.CharacterResource;
import dnd.manager.app.model.CharacterEntities.CharacterSkill;
import dnd.manager.app.model.CharacterEntities.CharacterAbility;
import dnd.manager.app.model.CharacterEntities.CharacterClass;
import dnd.manager.app.model.CharacterEntities.CharacterFeat;
import dnd.manager.app.model.CharacterEntities.CharacterFeature;
import dnd.manager.app.model.CharacterEntities.CharacterSpell;
import dnd.manager.app.model.CharacterEntities.CharacterSpellSlot;
import dnd.manager.app.model.CharacterEntities.CharacterStatus;
import dnd.manager.app.model.ClassEntities.ClassEntity;
import dnd.manager.app.model.ClassEntities.ClassFeature;
import dnd.manager.app.model.ClassEntities.ClassResource;
import dnd.manager.app.model.ClassEntities.SpellcastingType;
import dnd.manager.app.model.FeatureEntities.Feature;
import dnd.manager.app.model.Ability;
import dnd.manager.app.model.Feat;
import dnd.manager.app.model.ItemEntities.Item;
import dnd.manager.app.model.SpellcastingSlotEntities.SpellcastingSlot;
import dnd.manager.app.model.SubclassEntities.Subclass;
import dnd.manager.app.model.SubclassEntities.SubclassFeature;
import dnd.manager.app.repository.AbilityRepository;
import dnd.manager.app.repository.UserRepository;
import dnd.manager.app.repository.BackgroundRepositories.BackgroundRepository;
import dnd.manager.app.repository.CharacterRepositories.CharacterRepository;
import dnd.manager.app.repository.CharacterRepositories.CharacterSpellRepository;
import dnd.manager.app.repository.ClassRepositories.ClassFeatureRepository;
import dnd.manager.app.repository.ClassRepositories.ClassRepository;
import dnd.manager.app.repository.ClassRepositories.ClassResourceRepository;
import dnd.manager.app.repository.FeatRepositories.FeatRepository;
import dnd.manager.app.repository.FeatureRepositories.FeatureRepository;
import dnd.manager.app.repository.ItemRepositories.ItemRepository;
import dnd.manager.app.repository.CharacterRepositories.CharacterFeatRepository;
import dnd.manager.app.repository.CharacterRepositories.CharacterFeatureRepository;
import dnd.manager.app.repository.SkillRepositories.SkillRepository;
import dnd.manager.app.repository.SpeciesRepositories.SpeciesRepository;
import dnd.manager.app.repository.SpellRepositories.SpellRepository;
import dnd.manager.app.repository.SpellcastingSlotRepositories.SpellcastingSlotRepository;
import dnd.manager.app.repository.SubclassRepositories.SubclassFeatureRepository;
import dnd.manager.app.repository.SubclassRepositories.SubclassRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class CharacterService {

    private final CharacterRepository repository;
    private final UserRepository userRepository;
    private final AbilityRepository abilityRepository;
    private final SkillRepository skillRepository;
    private final ItemRepository itemRepository;
    private final CharacterMapper mapper;
    private final SpeciesRepository speciesRepository;
    private final BackgroundRepository backgroundRepository;
    private final CharacterSpellRepository characterSpellRepository;
    private final CharacterFeatRepository characterFeatRepository;
    private final FeatRepository featRepository;
    private final SpellRepository spellRepository;
    private final ClassRepository classRepository;
    private final SubclassRepository subclassRepository;
    private final ClassFeatureRepository classFeatureRepository;
    private final SubclassFeatureRepository subclassFeatureRepository;
    private final ClassResourceRepository classResourceRepository;
    private final SpellcastingSlotRepository spellcastingSlotRepository;
    private final FeatureRepository featureRepository;
    private final CharacterFeatureRepository characterFeatureRepository;
    
    public CharacterService(
        CharacterRepository repository,
        UserRepository userRepository,
        AbilityRepository abilityRepository,
        SkillRepository skillRepository,
        ItemRepository itemRepository,
        CharacterMapper mapper, 
        SpeciesRepository speciesRepository, 
        BackgroundRepository backgroundRepository,
        CharacterSpellRepository characterSpellRepository,
        CharacterFeatRepository characterFeatRepository,
        FeatRepository featRepository,
        SpellRepository spellRepository, 
        ClassRepository classRepository, 
        SubclassRepository subclassRepository, 
        ClassFeatureRepository classFeatureRepository, 
        SubclassFeatureRepository subclassFeatureRepository,
        ClassResourceRepository classResourceRepository, 
        SpellcastingSlotRepository spellcastingSlotRepository,
        FeatureRepository featureRepository, 
        CharacterFeatureRepository characterFeatureRepository
    ) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.abilityRepository = abilityRepository;
        this.skillRepository = skillRepository;
        this.itemRepository = itemRepository;
        this.mapper = mapper;
        this.speciesRepository = speciesRepository;
        this.backgroundRepository = backgroundRepository;
        this.characterSpellRepository = characterSpellRepository;
        this.characterFeatRepository = characterFeatRepository;
        this.featRepository = featRepository;
        this.spellRepository = spellRepository;
        this.classRepository = classRepository;
        this.subclassRepository = subclassRepository;
        this.classFeatureRepository = classFeatureRepository;
        this.subclassFeatureRepository = subclassFeatureRepository;
        this.classResourceRepository = classResourceRepository;
        this.spellcastingSlotRepository = spellcastingSlotRepository;
        this.featureRepository = featureRepository;
        this.characterFeatureRepository = characterFeatureRepository;
    }


    // Lista resumida de personajes de un usuario
    public List<CharacterSummaryDto> findByUserId(Long userId) {
        return repository.findByUserId(userId)
                .stream()
                .map(mapper::toSummaryDto)
                .toList();
    }

    // Detalle completo de un personaje
    public CharacterResponseDto findByUserIdAndId(Long userId, Long id) {
        CharacterEntity entity = repository.findByUserIdAndId(userId, id);
        return mapper.toResponseDto(entity);
    }

    // Crear personaje en modo DRAFT
    public CharacterSummaryDto create(Long userId, CharacterCreateDto dto) {
        User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
        
        CharacterEntity entity = mapper.toEntity(dto);
        entity.setUser(user);
        entity.setLevel(0);
        entity.setCurrentHp(0);
        entity.setMaxHp(0);
        entity.setMoney(0);
        entity.setExperience(0);
        entity.setStatus(CharacterStatus.DRAFT);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        return mapper.toSummaryDto(repository.save(entity));
    }

    // Actualizar parcialmente (wizard paso a paso)
    public CharacterResponseDto patch(Long userId, Long id, CharacterPatchDto dto) {
        
        CharacterEntity entity = repository.findByUserIdAndId(userId, id);
        
        if (entity == null) throw new RuntimeException("Character not found");        
        if (dto.name() != null) entity.setName(dto.name());
        if (dto.currentHp() != null) entity.setCurrentHp(dto.currentHp());
        if (dto.maxHp() != null) entity.setMaxHp(dto.maxHp());
        if (dto.money() != null) entity.setMoney(dto.money());
        if (dto.experience() != null) entity.setExperience(dto.experience());
        if (dto.walkSpeed() != null) entity.setWalkSpeed(dto.walkSpeed());
        if (dto.flySpeed() != null) entity.setFlySpeed(dto.flySpeed());
        if (dto.speciesId() != null) entity.setSpecies(speciesRepository.findById(dto.speciesId()).orElseThrow(() -> new RuntimeException("Species not found")));
        if (dto.backgroundId() != null) entity.setBackground(backgroundRepository.findById(dto.backgroundId()).orElseThrow(() -> new RuntimeException("Background not found")));
        entity.setUpdatedAt(LocalDateTime.now());

        return mapper.toResponseDto(repository.save(entity));
    }


    public void delete(Long userId, Long id) {
        CharacterEntity entity = repository.findByUserIdAndId(userId, id);
        repository.delete(entity);
    }


    // Replaces
    @Transactional
    public CharacterResponseDto replaceAbilities(Long userId, Long id, List<CharacterAbilityDto> dtos) {
        CharacterEntity entity = repository.findByUserIdAndId(userId, id);
        
        entity.getAbilities().clear();
        repository.flush();


        List<CharacterAbility> abilities = dtos.stream().map(dto -> {
                CharacterAbility ability = new CharacterAbility();
                ability.setCharacter(entity);
                ability.setAbility(abilityRepository.findByCode(dto.ability()).orElseThrow(() -> new RuntimeException("Ability not found: " + dto.ability())));
                ability.setBaseValue(dto.baseValue());
                return ability;
            }
        ).toList();
        
        entity.getAbilities().addAll(abilities);
        return mapper.toResponseDto(entity);
    }

    @Transactional
    public CharacterResponseDto replaceSkills(Long userId, Long id, List<CharacterSkillDto> dtos) {
        CharacterEntity entity = repository.findByUserIdAndId(userId, id);

        entity.getSkills().clear();
        repository.flush();

        List<CharacterSkill> skills = dtos.stream().map(dto -> {
                CharacterSkill skill = new CharacterSkill();
                skill.setCharacter(entity);
                skill.setSkill(skillRepository.findById(dto.skillId())
                    .orElseThrow(() -> new RuntimeException("Skill with id " + dto.skillId() + " not found")));
                skill.setProficiency(dto.proficient());
                skill.setExpertise(dto.expertise());
                return skill;
            }
        ).toList();

        entity.getSkills().addAll(skills);
        return mapper.toResponseDto(repository.save(entity));
    }


    //Item management
    @Transactional
    public CharacterResponseDto buyItem(Long userId, Long characterId, Long itemId, CharacterItemDto dto) {
        CharacterEntity character = repository.findByUserIdAndId(userId, characterId);
        if (character == null) throw new EntityNotFoundException("Character not found");

        Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> new EntityNotFoundException("Item not found"));

        int totalCost = item.getPrice() * (dto.quantity() != null ? dto.quantity() : 1);
        if (character.getMoney() < totalCost) {
            throw new RuntimeException("Not enough money to buy the item");
        }

        character.setMoney(character.getMoney() - totalCost);

        Optional<CharacterItem> existing = character.getItems().stream()
            .filter(ci -> ci.getItem().getId().equals(itemId))
            .findFirst();

        if (existing.isPresent()) {
            CharacterItem ci = existing.get();
            ci.setQuantity(ci.getQuantity() + (dto.quantity() != null ? dto.quantity() : 1));
        } else {
            CharacterItem ci = new CharacterItem();
            ci.setCharacter(character);
            ci.setItem(item);
            ci.setQuantity(dto.quantity() != null ? dto.quantity() : 1);
            ci.setEquipped(dto.equipped() != null ? dto.equipped() : false);
            ci.setAttuned(dto.attuned() != null ? dto.attuned() : false);
            character.getItems().add(ci);
        }

        return mapper.toResponseDto(repository.save(character));
    }

    public CharacterResponseDto addItem(Long userId, Long characterId, Long itemId, CharacterItemDto dto) {
        CharacterEntity character = repository.findByUserIdAndId(userId, characterId);
        if (character == null) throw new EntityNotFoundException("Character not found");

        Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> new EntityNotFoundException("Item not found"));

        Optional<CharacterItem> existing = character.getItems().stream()
            .filter(ci -> ci.getItem().getId().equals(itemId))
            .findFirst();



        if (existing.isPresent()) {
            CharacterItem ci = existing.get();
            ci.setQuantity(ci.getQuantity() + (dto != null && dto.quantity() != null ? dto.quantity() : 1));
        } else {
            CharacterItem ci = new CharacterItem();
            ci.setCharacter(character);
            ci.setItem(item);
            ci.setQuantity(dto.quantity() != null ? dto.quantity() : 1);
            ci.setEquipped(dto.equipped() != null ? dto.equipped() : false);
            ci.setAttuned(dto.attuned() != null ? dto.attuned() : false);
            character.getItems().add(ci);
        }

        return mapper.toResponseDto(repository.save(character));
    }

    public CharacterResponseDto updateItem(Long userId, Long characterId, Long itemId, CharacterItemDto dto) {
        CharacterEntity character = repository.findByUserIdAndId(userId, characterId);
        if (character == null) throw new EntityNotFoundException("Character not found");

        character.getItems().stream()
            .filter(ci -> ci.getItem().getId().equals(itemId))
            .findFirst()
            .ifPresent(ci -> {
                if (dto.quantity() != null) ci.setQuantity(dto.quantity());
                if (dto.equipped() != null) ci.setEquipped(dto.equipped());
                if (dto.attuned() != null) ci.setAttuned(dto.attuned());
            });

        return mapper.toResponseDto(repository.save(character));
    }

    public CharacterResponseDto removeItem(Long userId, Long characterId, Long itemId, Integer quantity) {
        CharacterEntity character = repository.findByUserIdAndId(userId, characterId);
        if (character == null) throw new EntityNotFoundException("Character not found");

        character.getItems().stream()
            .filter(ci -> ci.getItem().getId().equals(itemId))
            .findFirst()
            .ifPresent(ci -> {
                int newQty = ci.getQuantity() - (quantity != null ? quantity : 1);
                if (newQty <= 0) {
                    character.getItems().remove(ci);
                } else {
                    ci.setQuantity(newQty);
                }
            });

        return mapper.toResponseDto(repository.save(character));
    }


    //Spell management

    @Transactional
    public CharacterResponseDto addSpell(Long userId, Long characterId, Long spellId, CharacterSpellDto dto) {
        CharacterEntity character = repository.findByUserIdAndId(userId, characterId);
        if (character == null) throw new EntityNotFoundException("Character not found");

        dnd.manager.app.model.SpellEntities.Spell spell = spellRepository.findById(spellId)
            .orElseThrow(() -> new EntityNotFoundException("Spell not found"));

        if (character.getSpells() == null) {
            character.setSpells(new ArrayList<>());
        }

        Optional<CharacterSpell> existing = character.getSpells().stream()
            .filter(cs -> cs.getSpell().getId().equals(spellId))
            .findFirst();

        if (existing.isPresent()) {
            CharacterSpell cs = existing.get();
            if (dto != null) {
                if (dto.prepared() != null) cs.setPrepared(dto.prepared());
                if (dto.alwaysPrepared() != null) cs.setAlwaysPrepared(dto.alwaysPrepared());
            }
        } else {
            CharacterSpell cs = new CharacterSpell();
            cs.setCharacter(character);
            cs.setSpell(spell);
            cs.setPrepared(dto != null && dto.prepared() != null ? dto.prepared() : false);
            cs.setAlwaysPrepared(dto != null && dto.alwaysPrepared() != null ? dto.alwaysPrepared() : false);
            character.getSpells().add(cs);
            characterSpellRepository.save(cs);
        }

        return mapper.toResponseDto(repository.save(character));
    }

    @Transactional
    public CharacterResponseDto updateSpell(Long userId, Long characterId, Long spellId, CharacterSpellDto dto) {
        CharacterEntity character = repository.findByUserIdAndId(userId, characterId);
        if (character == null) throw new EntityNotFoundException("Character not found");

        character.getSpells().stream()
            .filter(cs -> cs.getSpell().getId().equals(spellId))
            .findFirst()
            .ifPresent(cs -> {
                if (dto.prepared() != null) cs.setPrepared(dto.prepared());
                if (dto.alwaysPrepared() != null) cs.setAlwaysPrepared(dto.alwaysPrepared());
            });

        return mapper.toResponseDto(repository.save(character));
    }

    @Transactional
    public CharacterResponseDto removeSpell(Long userId, Long characterId, Long spellId) {
        CharacterEntity character = repository.findByUserIdAndId(userId, characterId);
        if (character == null) throw new EntityNotFoundException("Character not found");

        character.getSpells().stream()
            .filter(cs -> cs.getSpell().getId().equals(spellId))
            .findFirst()
            .ifPresent(cs -> {
                character.getSpells().remove(cs);
                characterSpellRepository.delete(cs);
            });

        return mapper.toResponseDto(repository.save(character));
    }




    // Feat management

    @Transactional
    public CharacterResponseDto addFeat(Long userId, Long characterId, Long featId, CharacterFeatDto dto) {
        CharacterEntity character = repository.findByUserIdAndId(userId, characterId);
        if (character == null) throw new EntityNotFoundException("Character not found");

        Feat feat = featRepository.findById(featId)
            .orElseThrow(() -> new EntityNotFoundException("Feat not found"));

        if (character.getFeats() == null) {
            character.setFeats(new ArrayList<>());
        }

        if (character.getFeats().stream().anyMatch(cf -> cf.getFeat().getId().equals(featId)) && (feat.getRepeatable() == null || !feat.getRepeatable())) {
            throw new RuntimeException("Character already has this feat and it is not repeatable");
        }
        CharacterFeat characterFeat = new CharacterFeat();
        characterFeat.setCharacter(character);
        characterFeat.setFeat(feat);
        characterFeat.setSource(dto != null && dto.source() != null ? dto.source() : "manual");
        characterFeat.setSourceLevel(dto != null && dto.sourceLevel() != null ? dto.sourceLevel() : character.getLevel());
        character.getFeats().add(characterFeat);
        characterFeatRepository.save(characterFeat);

        return mapper.toResponseDto(repository.save(character));
    }
    
    @Transactional
    public CharacterResponseDto removeFeat(Long userId, Long characterId, Long featId) {
        CharacterEntity character = repository.findByUserIdAndId(userId, characterId);
        if (character == null) throw new EntityNotFoundException("Character not found");

        if (character.getFeats() != null) {
            character.getFeats().stream()
                .filter(cf -> cf.getFeat() != null && cf.getFeat().getId().equals(featId))
                .sorted((a, b) -> {
                    Integer aLevel = a.getSourceLevel();
                    Integer bLevel = b.getSourceLevel();
                    if (aLevel == null && bLevel == null) return 0;
                    if (aLevel == null) return 1;
                    if (bLevel == null) return -1;
                    return bLevel.compareTo(aLevel);
                })
                .findFirst()
                .ifPresent(cf -> {
                    character.getFeats().remove(cf);
                    characterFeatRepository.delete(cf);
                });
        }

        return mapper.toResponseDto(repository.save(character));
    }



    // Feature management

    @Transactional
    public CharacterResponseDto addFeature(Long userId, Long characterId, Long featureId) {
        CharacterEntity character = repository.findByUserIdAndId(userId, characterId);
        if (character == null) throw new EntityNotFoundException("Character not found");

        Feature feature = featureRepository.findById(featureId)
            .orElseThrow(() -> new EntityNotFoundException("Feature not found"));

        if (character.getFeatures() == null) {
            character.setFeatures(new ArrayList<>());
        }

        if (character.getFeatures().stream().anyMatch(cf -> cf.getFeature().getId().equals(featureId))) {
            throw new RuntimeException("Character already has this feature");
        }
        CharacterFeature characterFeature = new CharacterFeature();
        characterFeature.setCharacter(character);
        characterFeature.setFeature(feature);
        character.getFeatures().add(characterFeature);
        characterFeatureRepository.save(characterFeature);

        return mapper.toResponseDto(repository.save(character));
    }

    @Transactional
    public CharacterResponseDto removeFeature(Long userId, Long characterId, Long featureId) {
        CharacterEntity character = repository.findByUserIdAndId(userId, characterId);
        if (character == null) throw new EntityNotFoundException("Character not found");

        if (character.getFeatures() != null) {
            character.getFeatures().stream()
                .filter(cf -> cf.getFeature() != null && cf.getFeature().getId().equals(featureId))
                .findFirst()
                .ifPresent(cf -> {
                    character.getFeatures().remove(cf);
                    characterFeatureRepository.delete(cf);
                });
        }

        return mapper.toResponseDto(repository.save(character));
    }



    // Classes and Subclasses management

    public CharacterResponseDto addClass(Long userId, Long characterId, Long classId) {
        CharacterEntity character = repository.findByUserIdAndId(userId, characterId);
        if (character == null) throw new EntityNotFoundException("Character not found");

        ClassEntity classEntity = classRepository.findById(classId)
            .orElseThrow(() -> new EntityNotFoundException("Class not found"));

        boolean hasClass = character.getClasses().stream()
            .anyMatch(cc -> cc.getClassEntity().getId().equals(classId));
        if (hasClass) {
            throw new RuntimeException("Character already has this class");
        }

        CharacterClass characterClass = new CharacterClass();
        characterClass.setCharacter(character);
        characterClass.setClassEntity(classEntity);
        characterClass.setLevel(1);
        character.getClasses().add(characterClass);
        
        character.setLevel(calculateLevelFromClasses(character.getClasses()));
        
        addClassFeatures(character, classId, 1);
        updateClassResources(character, classId, 1);
        updateMulticlassSpellSlots(character);

        return mapper.toResponseDto(repository.save(character));
    }

    public CharacterResponseDto addSubclass(Long userId, Long characterId, Long classId, Long subclassId) {
        CharacterEntity character = repository.findByUserIdAndId(userId, characterId);
        if (character == null) throw new EntityNotFoundException("Character not found");

        classRepository.findById(classId)
            .orElseThrow(() -> new EntityNotFoundException("Class not found"));

        Subclass subclass = subclassRepository.findById(subclassId)
            .orElseThrow(() -> new EntityNotFoundException("Subclass not found"));

        CharacterClass characterClass = character.getClasses().stream()
            .filter(cc -> cc.getClassEntity().getId().equals(classId))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Character does not have this class"));

        if (characterClass.getSubclass() != null) {
            throw new RuntimeException("Character already has a subclass for this class");
        }

        characterClass.setSubclass(subclass);

        return mapper.toResponseDto(repository.save(character));
    }

    private int calculateLevelFromClasses(List<CharacterClass> classes) {
        return classes.stream()
            .mapToInt(CharacterClass::getLevel)
            .sum();
    }

    public CharacterResponseDto removeClass(Long userId, Long characterId, Long classId) {
        CharacterEntity character = repository.findByUserIdAndId(userId, characterId);
        if (character == null) throw new EntityNotFoundException("Character not found");

        CharacterClass characterClass = character.getClasses().stream()
            .filter(cc -> cc.getClassEntity().getId().equals(classId))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Character does not have this class"));

        removeClassResources(character, classId);
        character.getClasses().remove(characterClass);
        character.setLevel(calculateLevelFromClasses(character.getClasses()));

        return mapper.toResponseDto(repository.save(character));
    }

    public CharacterResponseDto levelUpClass(Long userId, Long characterId, Long classId) {
        CharacterEntity character = repository.findByUserIdAndId(userId, characterId);
        
        if (character == null) throw new EntityNotFoundException("Character not found");

        CharacterClass characterClass = character.getClasses().stream()
            .filter(cc -> cc.getClassEntity().getId().equals(classId))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Character does not have this class"));

        characterClass.setLevel(characterClass.getLevel() + 1);
        character.setLevel(calculateLevelFromClasses(character.getClasses()));
        addClassFeatures(character, classId, characterClass.getLevel());
        updateClassResources(character, classId, characterClass.getLevel());
        updateMulticlassSpellSlots(character);
        
        if (characterClass.getSubclass() != null) {
            addSubclassFeatures(character, characterClass.getSubclass().getId(), characterClass.getLevel());
        }

        return mapper.toResponseDto(repository.save(character));

    }

    public CharacterResponseDto levelDownClass(Long userId, Long characterId, Long classId) {
        CharacterEntity character = repository.findByUserIdAndId(userId, characterId);
        if (character == null) throw new EntityNotFoundException("Character not found");

        CharacterClass characterClass = character.getClasses().stream()
            .filter(cc -> cc.getClassEntity().getId().equals(classId))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Character does not have this class"));

        if (characterClass.getLevel() <= 1) {
            removeClassFeatures(character, classId, 1);
            return removeClass(userId, characterId, classId);
        }

        characterClass.setLevel(characterClass.getLevel() - 1);
        character.setLevel(calculateLevelFromClasses(character.getClasses()));
        removeClassFeatures(character, classId, characterClass.getLevel() + 1);
        updateClassResources(character, classId, characterClass.getLevel());
        removeSubclassFeatures(character, characterClass.getSubclass() != null ? characterClass.getSubclass().getId() : null, characterClass.getLevel() + 1);
        updateMulticlassSpellSlots(character);

        return mapper.toResponseDto(repository.save(character));
    }



    // Helper methods to add and remove class features when leveling up or down
    private void addClassFeatures(CharacterEntity character, Long classId, Integer level) {
        List<ClassFeature> classFeatures =
            classFeatureRepository.findByClassEntityIdAndLevel(classId, level);

        for (ClassFeature classFeature : classFeatures) {

            Feature feature = classFeature.getFeature();

            boolean alreadyHasFeature = character.getFeatures().stream()
                .anyMatch(cf ->
                    cf.getFeature().getId().equals(feature.getId())
                );

            if (alreadyHasFeature) {
                continue;
            }

            CharacterFeature characterFeature = new CharacterFeature();
            characterFeature.setCharacter(character);
            characterFeature.setFeature(feature);

            character.getFeatures().add(characterFeature);
        }
    }

    private void removeClassFeatures(CharacterEntity character, Long classId, Integer level) {
        List<ClassFeature> classFeatures = classFeatureRepository.findByClassEntityIdAndLevel(classId, level );

        for (ClassFeature classFeature : classFeatures) {

            Long featureId = classFeature.getFeature().getId();
            character.getFeatures().removeIf(cf -> cf.getFeature().getId().equals(featureId));
        }
    }

    

    // Helper methods to add and remove subclass features when leveling up or down
    private void addSubclassFeatures(CharacterEntity character, Long subclassId, Integer level) {
        List<SubclassFeature> subclassFeatures =
            subclassFeatureRepository.findBySubclassIdAndLevel(subclassId, level);

        for (SubclassFeature subclassFeature : subclassFeatures) {
            Feature feature = subclassFeature.getFeature();

            boolean alreadyHasFeature = character.getFeatures().stream()
                .anyMatch(cf ->
                    cf.getFeature().getId().equals(feature.getId())
                );

            if (alreadyHasFeature) {
                continue;
            }

            CharacterFeature characterFeature = new CharacterFeature();
            characterFeature.setCharacter(character);
            characterFeature.setFeature(feature);

            character.getFeatures().add(characterFeature);
        }
    }

    private void removeSubclassFeatures(CharacterEntity character, Long subclassId, Integer level) {
        List<SubclassFeature> subclassFeatures = subclassFeatureRepository.findBySubclassIdAndLevel(subclassId, level );

        for (SubclassFeature subclassFeature : subclassFeatures) {
            Long featureId = subclassFeature.getFeature().getId();
            character.getFeatures().removeIf(cf -> cf.getFeature().getId().equals(featureId));
        }
    }



    // Helper method to add and remove resources when leveling up or down
    private void updateClassResources(CharacterEntity character, Long classId, Integer level) {
        List<ClassResource> classResources = classResourceRepository.findByClassEntityIdAndLevel(classId, level);

        for (ClassResource classResource : classResources) {

            Integer maxValue = parseResourceValue(classResource.getValue());

            CharacterResource characterResource = character.getResources().stream()
                .filter(cr -> cr.getName().equals(classResource.getName()) && cr.getClassEntity().getId().equals(classId))
                .findFirst()
                .orElse(null);

            if (characterResource == null) {
                characterResource = new CharacterResource();

                characterResource.setCharacter(character);
                characterResource.setClassEntity(classResource.getClassEntity());
                characterResource.setName(classResource.getName());
                characterResource.setMaxValue(maxValue);
                characterResource.setCurrentValue(maxValue);

                character.getResources().add(characterResource);

            } else {
                characterResource.setMaxValue(maxValue);

                if (characterResource.getCurrentValue() > maxValue) {
                    characterResource.setCurrentValue(maxValue);
                }
            }
        }
    }

    private void removeClassResources(CharacterEntity character, Long classId) {
        character.getResources().removeIf(
            resource ->
                resource.getClassEntity().getId().equals(classId)
        );
    }

    private Integer parseResourceValue(String value) {
        if (value == null || value.isBlank()) {
            return 0;
        }

        String number = value.replaceFirst("[^0-9].*$", "");

        return number.isEmpty() ? 0 : Integer.parseInt(number);
    }

    private void updateMulticlassSpellSlots(CharacterEntity character) {
        int casterLevel = 0;

        for (CharacterClass cc : character.getClasses()) {
            int level = cc.getLevel();

            SpellcastingType type = cc.getClassEntity().getSpellcastingType();

            if (type == null && cc.getSubclass() != null) {
                type = cc.getSubclass().getSpellcastingType();
            }

            if (type == null) {
                continue;
            }

            switch (type) {
                case FULL  -> casterLevel += level;
                case HALF  -> casterLevel += level / 2;
                case THIRD -> casterLevel += level / 3;
                default -> {}
            }
        }

        character.getSpellSlots().clear();

        if (casterLevel == 0) {
            return;
        }

        List<SpellcastingSlot> slots =
            spellcastingSlotRepository.findByCasterLevel(casterLevel);

        for (SpellcastingSlot slot : slots) {
            CharacterSpellSlot spellSlot = new CharacterSpellSlot();

            spellSlot.setCharacter(character);
            spellSlot.setSpellLevel(slot.getSpellLevel());
            spellSlot.setMaxSlots(slot.getSlots());
            spellSlot.setCurrentSlots(slot.getSlots());

            character.getSpellSlots().add(spellSlot);
        }
    }
    
    
    // Finalizar personaje
    public CharacterResponseDto finalize(Long userId, Long id) {
        CharacterEntity entity = repository.findByUserIdAndId(userId, id);

        entity.setStatus(CharacterStatus.FINAL);
        entity.setUpdatedAt(LocalDateTime.now());
        entity.setFinalizedAt(LocalDateTime.now());
        return mapper.toResponseDto(repository.save(entity));
    }
}