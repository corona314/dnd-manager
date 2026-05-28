package dnd.manager.app.dto.CharacterDto;

import java.util.List;

/*
    Clase con atributos que puede editar el usuario
*/

public record CharacterPatchDto(
    String name,
    Integer maxHp,
    Integer currentHp,
    Integer walkSpeed,
    Integer flySpeed,
    Long speciesId,
    Long classId,
    Long subclassId,
    Long backgroundId,
    List<CharacterStatDto> stats,
    List<CharacterSkillDto> skills
    
) {}
