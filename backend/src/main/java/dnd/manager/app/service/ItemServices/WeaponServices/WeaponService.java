package dnd.manager.app.service.ItemServices.WeaponServices;

import dnd.manager.app.dto.ItemDto.ItemResponseDto;
import dnd.manager.app.dto.ItemDto.WeaponDto.WeaponSummaryDto;
import dnd.manager.app.mapper.ItemMapper;
import dnd.manager.app.model.ItemEntities.Item;
import dnd.manager.app.repository.ItemRepositories.ItemRepository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import static dnd.manager.app.repository.ItemRepositories.spec.ItemSpecifications.*;
import static dnd.manager.app.repository.ItemRepositories.WeaponRepositories.spec.WeaponSpecifications.*;

@Service
public class WeaponService {

    private final ItemRepository itemRepository;
    private final ItemMapper mapper;

    public WeaponService(ItemRepository itemRepository, ItemMapper mapper) {
        this.itemRepository = itemRepository;
        this.mapper = mapper;
    }

    public ItemResponseDto findById(Long id) {
        Item item = itemRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!"Weapon".equals(item.getItemType().getName())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return mapper.toResponseDto(item);
    }


    public Page<WeaponSummaryDto> findWeapons(
        String name,
        Float weightMin,
        Float weightMax,
        Integer priceMin,
        Integer priceMax,
        Boolean magic,
        Boolean attunement,
        List<String> rarity,
        Integer rangeMin,
        Integer rangeMax,
        Integer rangeNormalMin,
        Integer rangeNormalMax,
        Integer rangeLongMin,
        Integer rangeLongMax,
        String mastery,
        List<String> damageTypes,
        int page,
        int size
    ){
        Specification<Item> spec = Specification
        .where(hasName(name))
        .and(hasWeightBetween(weightMin, weightMax))
        .and(hasPriceBetween(priceMin, priceMax))
        .and(hasItemType(List.of("Weapon")))
        .and(isMagic(magic))
        .and(hasAttunement(attunement))
        .and(hasRarity(rarity))
        .and(hasRangeBetween(rangeMin, rangeMax))
        .and(hasRangeNormalBetween(rangeNormalMin, rangeNormalMax))
        .and(hasRangeLongBetween(rangeLongMin, rangeLongMax))
        .and(hasMastery(mastery))
        .and(hasDamage(damageTypes));
        
        Page<Item> weapons = itemRepository.findAll(
                    spec,
                    PageRequest.of(page, size)
            );
        return weapons.map(mapper::toWeaponSummaryDto);
    }

}