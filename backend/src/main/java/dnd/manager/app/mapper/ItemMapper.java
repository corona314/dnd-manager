package dnd.manager.app.mapper;

import org.springframework.stereotype.Component;

import dnd.manager.app.dto.ItemDto.ItemResponseDto;
import dnd.manager.app.dto.ItemDto.ItemSummaryDto;
import dnd.manager.app.model.ItemEntities.Item;

@Component
public class ItemMapper {


    public ItemResponseDto toResponseDto(Item i){
        return new ItemResponseDto(
            i.getName(),
            i.getWeight(),
            i.getPrice(),
            i.getItemType() == null ? null : i.getItemType().getName(),
            i.getMagic(),
            i.getAttunement(),
            i.getRarity()
        );
    }

    public ItemSummaryDto toSummaryDto(Item i){
        return new ItemSummaryDto(
            i.getName(),
            i.getWeight(),
            i.getPrice(),
            i.getItemType() == null ? null : i.getItemType().getName(),
            i.getMagic(),
            i.getAttunement(),
            i.getRarity()
        );
    }

}
