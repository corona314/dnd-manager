package dnd.manager.app.service.ItemServices;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import dnd.manager.app.dto.ItemDto.ItemResponseDto;
import dnd.manager.app.dto.ItemDto.ShieldSummaryDto;
import dnd.manager.app.mapper.ItemMapper;
import dnd.manager.app.model.ItemEntities.Item;
import dnd.manager.app.repository.ItemRepositories.ItemRepository;
import static dnd.manager.app.repository.ItemRepositories.spec.ItemSpecifications.*;
import static dnd.manager.app.repository.ItemRepositories.spec.ShieldSpecifications.*;

import java.util.List;

@Service
public class ShieldService {

    private final ItemRepository itemRepository;
    private final ItemMapper mapper;


    public ShieldService(ItemRepository itemRepository, ItemMapper mapper) {
        this.itemRepository = itemRepository;
        this.mapper = mapper;
    }
    
    public ItemResponseDto findById(Long id) {
        Item item = itemRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!"Shield".equals(item.getItemType().getName())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return mapper.toResponseDto(item);
    }

    public Page<ShieldSummaryDto> findShields(
        String name,
        Float weightMin,
        Float weightMax,
        Integer priceMin,
        Integer priceMax,
        Boolean magic,
        Boolean attunement,
        List<String> rarity,
        Integer acBonus,
        int page,
        int size
    ){
        Specification<Item> spec = Specification
        .where(hasName(name))
        .and(hasWeightBetween(weightMin, weightMax))
        .and(hasPriceBetween(priceMin, priceMax))
        .and(hasItemType(List.of("Shield")))
        .and(isMagic(magic))
        .and(hasAttunement(attunement))
        .and(hasRarity(rarity))
        .and(hasAcBonus(acBonus));
        
        Page<Item> shields = itemRepository.findAll(
                    spec,
                    PageRequest.of(page, size)
            );
        return shields.map(mapper::toShieldSummaryDto);
    }


}
