package dnd.manager.app.dto.CharacterDto;

import dnd.manager.app.dto.ClassDto.ClassSummaryDto;
import dnd.manager.app.dto.ClassDto.SubclassSummaryDto;

/*
    Clase con toda la información del personaje
*/

public record CharacterClassResponseDto(
    ClassSummaryDto classEntity,
    SubclassSummaryDto subclass,
    Integer level
) {}