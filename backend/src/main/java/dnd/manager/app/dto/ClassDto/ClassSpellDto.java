package dnd.manager.app.dto.ClassDto;

import java.util.List;


public record ClassSpellDto (
    List<ClassSummaryDto> spells
){}
