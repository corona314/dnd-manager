package dnd.manager.app.service.ItemServices;

import dnd.manager.app.dto.ItemDto.ItemResponseDto;
import dnd.manager.app.dto.ItemDto.ItemSummaryDto;
import dnd.manager.app.mapper.ItemMapper;
import dnd.manager.app.model.ItemEntities.Item;
import dnd.manager.app.repository.ItemRepositories.ItemRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import static dnd.manager.app.repository.ItemRepositories.spec.ItemSpecifications.*;

import java.util.List;


@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final ItemMapper mapper;

    public ItemService(ItemRepository objectRepository, ItemMapper mapper) {
        this.itemRepository = objectRepository;
        this.mapper = mapper;
    }

    public ItemResponseDto findById(Long id) {
        return mapper.toResponseDto(itemRepository.findById(id).orElse(null));
    }

    public Page<ItemSummaryDto> findItems(
        String name,
        Float weightMin,
        Float weightMax,
        Integer priceMin,
        Integer priceMax,
        List<String> itemType,
        Boolean magic,
        Boolean attunement,
        List<String> rarity,
        Pageable pageable
    ){
        Specification<Item> spec = Specification
        .where(hasName(name))
        .and(hasWeightBetween(weightMin, weightMax))
        .and(hasPriceBetween(priceMin, priceMax))
        .and(hasItemType(itemType))
        .and(hasNotItemType(List.of("Weapon","Armor","Shield")))
        .and(isMagic(magic))
        .and(hasAttunement(attunement))
        .and(hasRarity(rarity));
        
        Page<Item> items = itemRepository.findAll(spec, pageable);
        
        return items.map(mapper::toSummaryDto);
    }
}