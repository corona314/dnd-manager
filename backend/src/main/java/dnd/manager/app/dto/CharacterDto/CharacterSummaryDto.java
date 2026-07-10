package dnd.manager.app.dto.CharacterDto;

import java.time.LocalDateTime;

import dnd.manager.app.model.CharacterEntities.CharacterStatus;

/*
    Clase necesaria para poder ver la información de tus personajes
    Tarjeta rápida para consultar
*/

public record CharacterSummaryDto (
    Long id,
    String name,
    Integer level,
    CharacterStatus status,
    LocalDateTime updatedAt

){}
