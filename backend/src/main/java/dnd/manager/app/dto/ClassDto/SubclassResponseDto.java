package dnd.manager.app.dto.ClassDto;

import java.util.List;

public record SubclassResponseDto (
    String name,
    List<SubclassFeatureDto> features,
    List<SubclassSpellDto> spells
){}
