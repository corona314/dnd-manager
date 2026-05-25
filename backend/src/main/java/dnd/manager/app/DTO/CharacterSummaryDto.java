package dnd.manager.app.DTO;

import java.time.LocalDateTime;

import dnd.manager.app.model.CharacterEntities.CharacterStatus;

public record CharacterSummaryDto (
    Long id,
    String name,
    Integer level,
    CharacterStatus status,
    LocalDateTime updatedAt

){}
