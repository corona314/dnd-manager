package dnd.manager.app.service.CharacterServices;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import dnd.manager.app.dto.CharacterDto.CharacterCreateDto;
import dnd.manager.app.dto.CharacterDto.CharacterPatchDto;
import dnd.manager.app.dto.CharacterDto.CharacterResponseDto;
import dnd.manager.app.dto.CharacterDto.CharacterAbilityDto;
import dnd.manager.app.dto.CharacterDto.CharacterSummaryDto;
import dnd.manager.app.mapper.CharacterMapper;
import dnd.manager.app.model.User;
import dnd.manager.app.model.CharacterEntities.CharacterEntity;
import dnd.manager.app.model.CharacterEntities.CharacterAbility;
import dnd.manager.app.model.CharacterEntities.CharacterAbilityus;
import dnd.manager.app.repository.AbilityRepository;
import dnd.manager.app.repository.UserRepository;
import dnd.manager.app.repository.CharacterRepositories.CharacterRepository;
import dnd.manager.app.repository.CharacterRepositories.CharacterAbilityRepository;
import jakarta.transaction.Transactional;

@Service
public class CharacterService {

    private final CharacterRepository repository;
    private final UserRepository userRepository;
    private final CharacterAbilityRepository characterAbilityRepository;
    private final AbilityRepository abilityRepository;
    private final CharacterMapper mapper;

    public CharacterService(
        CharacterRepository repository,
        UserRepository userRepository,
        CharacterAbilityRepository characterAbilityRepository,
        AbilityRepository abilityRepository,
        CharacterMapper mapper
    ) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.characterAbilityRepository = characterAbilityRepository;
        this.abilityRepository = abilityRepository;
        this.mapper = mapper;
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
    public CharacterResponseDto create(Long userId, CharacterCreateDto dto) {
        User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
        
        CharacterEntity entity = mapper.toEntity(dto);
        entity.setUser(user);
        entity.setLevel(0);
        entity.setStatus(CharacterAbilityus.DRAFT);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        return mapper.toResponseDto(repository.save(entity));
    }

    // Actualizar parcialmente (wizard paso a paso)
    public CharacterResponseDto patch(Long userId, Long id, CharacterPatchDto dto) {
        
        CharacterEntity entity = repository.findByUserIdAndId(userId, id);
        
        if (entity == null) throw new RuntimeException("Character not found");        
        if (dto.name() != null) entity.setName(dto.name());
        if (dto.maxHp() != null) entity.setMaxHp(dto.maxHp());
        if (dto.currentHp() != null) entity.setCurrentHp(dto.currentHp());
        if (dto.walkSpeed() != null) entity.setWalkSpeed(dto.walkSpeed());
        if (dto.flySpeed() != null) entity.setFlySpeed(dto.flySpeed());
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
        
        List<CharacterAbility> abilities = dtos.stream().map(dto -> {
                CharacterAbility ability = new CharacterAbility();
                ability.setCharacter(entity);
                ability.setAbility(abilityRepository.findById(dto.abilityId()).orElseThrow(() -> new RuntimeException("Ability not found: " + dto.abilityId())));
                ability.setBaseValue(dto.baseValue());
                return ability;
            }
        ).toList();
        characterAbilityRepository.deleteByCharacterId(id);
        characterAbilityRepository.saveAll(abilities);
        entity.setAbilities(abilities);
        return mapper.toResponseDto(entity);
    }






    // Finalizar personaje
    public CharacterResponseDto finalize(Long userId, Long id) {
        CharacterEntity entity = repository.findByUserIdAndId(userId, id);

        entity.setStatus(CharacterAbilityus.FINAL);
        entity.setUpdatedAt(LocalDateTime.now());
        entity.setFinalizedAt(LocalDateTime.now());
        entity.setLevel(entity.getLevel()+1);
        return mapper.toResponseDto(repository.save(entity));
    }

}