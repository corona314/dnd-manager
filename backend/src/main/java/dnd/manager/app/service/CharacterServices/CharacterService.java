package dnd.manager.app.service.CharacterServices;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import dnd.manager.app.dto.CharacterDto.CharacterCreateDto;
import dnd.manager.app.dto.CharacterDto.CharacterPatchDto;
import dnd.manager.app.dto.CharacterDto.CharacterResponseDto;
import dnd.manager.app.dto.CharacterDto.CharacterStatDto;
import dnd.manager.app.dto.CharacterDto.CharacterSummaryDto;
import dnd.manager.app.mapper.CharacterMapper;
import dnd.manager.app.model.User;
import dnd.manager.app.model.CharacterEntities.CharacterEntity;
import dnd.manager.app.model.CharacterEntities.CharacterStat;
import dnd.manager.app.model.CharacterEntities.CharacterStatus;
import dnd.manager.app.repository.StatRepository;
import dnd.manager.app.repository.UserRepository;
import dnd.manager.app.repository.CharacterRepositories.CharacterRepository;
import dnd.manager.app.repository.CharacterRepositories.CharacterStatRepository;
import jakarta.transaction.Transactional;

@Service
public class CharacterService {

    private final CharacterRepository repository;
    private final UserRepository userRepository;
    private final CharacterStatRepository characterStatRepository;
    private final StatRepository statRepository;
    private final CharacterMapper mapper;

    public CharacterService(
        CharacterRepository repository,
        UserRepository userRepository,
        CharacterStatRepository characterStatRepository,
        StatRepository statRepository,
        CharacterMapper mapper
    ) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.characterStatRepository = characterStatRepository;
        this.statRepository = statRepository;
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
        entity.setStatus(CharacterStatus.DRAFT);
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
    public CharacterResponseDto replaceStats(Long userId, Long id, List<CharacterStatDto> dtos) {
        CharacterEntity entity = repository.findByUserIdAndId(userId, id);
        
        List<CharacterStat> stats = dtos.stream().map(dto -> {
                CharacterStat stat = new CharacterStat();
                stat.setCharacter(entity);
                stat.setStat(statRepository.findById(dto.statId()).orElseThrow(() -> new RuntimeException("Stat not found: " + dto.statId())));
                stat.setBaseValue(dto.baseValue());
                return stat;
            }
        ).toList();
        characterStatRepository.deleteByCharacterId(id);
        characterStatRepository.saveAll(stats);
        entity.setStats(stats);
        return mapper.toResponseDto(entity);
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