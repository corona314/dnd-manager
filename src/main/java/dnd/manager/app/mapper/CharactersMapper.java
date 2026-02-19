package dnd.manager.app.mapper;

import dnd.manager.app.DTO.CharactersDTO;
import dnd.manager.app.model.Characters;

public class CharactersMapper {
    public static CharactersDTO toDTO(Characters c) {
        return new CharactersDTO(c.getId(), c.getUser_id(), c.getMax_hp(), c.getCurrent_hp(), c.getSpecie_id(), 
        c.getClass_id(), c.getSubclass_id(), c.getCreated_at(), c.getName(), c.getLevel(), c.getSpeed());
    }

    public static Characters toEntity(CharactersDTO c) {
        return new Characters(c.getId(), c.getUser_id(), c.getMax_hp(), c.getCurrent_hp(), c.getSpecie_id(), 
        c.getClass_id(), c.getSubclass_id(), c.getCreated_at(), c.getName(), c.getLevel(), c.getSpeed());
    } 
}
