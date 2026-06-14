package dnd.manager.app.dto.ClassDto;

import dnd.manager.app.dto.FeatureDto;

public record ClassFeatureDto (
    FeatureDto feature,
    Integer level
){}
