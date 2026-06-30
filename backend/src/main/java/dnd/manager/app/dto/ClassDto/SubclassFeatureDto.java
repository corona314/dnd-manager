package dnd.manager.app.dto.ClassDto;

import dnd.manager.app.dto.FeatureDto;

public record SubclassFeatureDto (
    FeatureDto feature,
    Integer level
){}
