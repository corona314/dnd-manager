package dnd.manager.app.service.CharacterServices;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import dnd.manager.app.dto.CharacterDto.CharacterCreateDto;
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
import dnd.manager.app.model.CharacterEntities.CharacterSkill;
import dnd.manager.app.model.CharacterEntities.CharacterAbility;
import dnd.manager.app.model.CharacterEntities.CharacterSpell;
import dnd.manager.app.model.CharacterEntities.CharacterStatus;
import dnd.manager.app.model.ItemEntities.Item;
import dnd.manager.app.repository.AbilityRepository;
import dnd.manager.app.repository.SkillRepository;
import dnd.manager.app.repository.UserRepository;
import dnd.manager.app.repository.BackgroundRepositories.BackgroundRepository;
import dnd.manager.app.repository.CharacterRepositories.CharacterRepository;
import dnd.manager.app.repository.CharacterRepositories.CharacterSpellRepository;
import dnd.manager.app.repository.ClassRepositories.ClassRepository;
import dnd.manager.app.repository.ItemRepositories.ItemRepository;
import dnd.manager.app.repository.SpeciesRepositories.SpeciesRepository;
import dnd.manager.app.repository.SpellRepositories.SpellRepository;
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
    private final ClassRepository classRepository;
    private final SubclassRepository subclassRepository;
    private final BackgroundRepository backgroundRepository;
    private final CharacterSpellRepository characterSpellRepository;
    private final SpellRepository spellRepository;

    public CharacterService(
        CharacterRepository repository,
        UserRepository userRepository,
        AbilityRepository abilityRepository,
        SkillRepository skillRepository,
        ItemRepository itemRepository,
        CharacterMapper mapper, 
        SpeciesRepository speciesRepository, 
        ClassRepository classRepository, 
        SubclassRepository subclassRepository,
        BackgroundRepository backgroundRepository,
        CharacterSpellRepository characterSpellRepository,
        SpellRepository spellRepository
    ) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.abilityRepository = abilityRepository;
        this.skillRepository = skillRepository;
        this.itemRepository = itemRepository;
        this.mapper = mapper;
        this.speciesRepository = speciesRepository;
        this.classRepository = classRepository;
        this.subclassRepository = subclassRepository;
        this.backgroundRepository = backgroundRepository;
        this.characterSpellRepository = characterSpellRepository;
        this.spellRepository = spellRepository;
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
        if (dto.classId() != null) entity.setClassEntity(classRepository.findById(dto.classId()).orElseThrow(() -> new RuntimeException("Class not found")));
        if (dto.subclassId() != null) entity.setSubclass(subclassRepository.findById(dto.subclassId()).orElseThrow(() -> new RuntimeException("Subclass not found")));
        if (dto.backgroundId() != null) entity.setBackground(backgroundRepository.findById(dto.backgroundId()).orElseThrow(() -> new RuntimeException("Background not found")));
        entity.setUpdatedAt(LocalDateTime.now());

        return mapper.toResponseDto(repository.save(entity));
    }


    public void delete(Long userId, Long id) {
        CharacterEntity entity = repository.findByUserIdAndId(userId, id);
        repository.delete(entity);
    }

    @Transactional
    public CharacterResponseDto replaceAbilities(Long userId, Long id, List<CharacterAbilityDto> dtos) {
        CharacterEntity entity = repository.findByUserIdAndId(userId, id);
        
        entity.getAbilities().clear();

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
                skill.setProficient(dto.proficient());
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

    // Finalizar personaje
    public CharacterResponseDto finalize(Long userId, Long id) {
        CharacterEntity entity = repository.findByUserIdAndId(userId, id);

        entity.setStatus(CharacterStatus.FINAL);
        entity.setUpdatedAt(LocalDateTime.now());
        entity.setFinalizedAt(LocalDateTime.now());
        entity.setLevel(entity.getLevel()+1);
        return mapper.toResponseDto(repository.save(entity));
    }
}