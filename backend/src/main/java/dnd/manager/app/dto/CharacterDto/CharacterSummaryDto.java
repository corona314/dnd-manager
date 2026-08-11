package dnd.manager.app.dto.CharacterDto;

import java.time.LocalDateTime;
import java.util.List;

import dnd.manager.app.model.CharacterEntities.CharacterStatus;

/*
    Clase necesaria para poder ver la información de tus personajes
    Tarjeta rápida para consultar
*/

public record CharacterSummaryDto (
    Long id,
    String name,
    Integer level,
    List<String> classes,
    String species,
    CharacterStatus status,
    LocalDateTime updatedAt

){}
