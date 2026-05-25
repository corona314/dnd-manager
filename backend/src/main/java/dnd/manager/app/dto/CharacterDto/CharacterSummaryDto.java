package dnd.manager.app.dto.CharacterDto;

import java.time.LocalDateTime;

import dnd.manager.app.model.CharacterEntities.CharacterStatus;

public record CharacterSummaryDto (
    Long id,
    String name,
    Integer level,
    CharacterStatus status,
    LocalDateTime updatedAt

){}
