package dnd.manager.app.service.ItemServices.ArmorServices;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import dnd.manager.app.dto.ItemDto.ItemResponseDto;
import dnd.manager.app.dto.ItemDto.ArmorDto.ArmorSummaryDto;
import dnd.manager.app.mapper.ItemMapper;
import dnd.manager.app.model.ItemEntities.Item;
import dnd.manager.app.repository.ItemRepositories.ItemRepository;

import static dnd.manager.app.repository.ItemRepositories.spec.ItemSpecifications.*;

import java.util.List;

import static dnd.manager.app.repository.ItemRepositories.ArmorRepositories.spec.ArmorSpecifications.*;

@Service
public class ArmorService {

    private final ItemRepository itemRepository;
    private final ItemMapper mapper;


    public ArmorService(ItemRepository itemRepository, ItemMapper mapper) {
        this.itemRepository = itemRepository;
        this.mapper = mapper;
    }

    public ItemResponseDto findById(Long id) {
        Item item = itemRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!"Armor".equals(item.getItemType().getName())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return mapper.toResponseDto(item);
    }

    public Page<ArmorSummaryDto> findArmor(
        String name,
        Float weightMin,
        Float weightMax,
        Integer priceMin,
        Integer priceMax,
        Boolean magic,
        Boolean attunement,
        List<String> rarity,
        Integer acMin,
        Integer acMax,
        Integer str,
        Boolean stealthDis,
        List<String> armorType,
        Pageable pageable
    ){
        Specification<Item> spec = Specification
        .where(hasName(name))
        .and(hasWeightBetween(weightMin, weightMax))
        .and(hasPriceBetween(priceMin, priceMax))
        .and(hasItemType(List.of("Armor")))
        .and(isMagic(magic))
        .and(hasAttunement(attunement))
        .and(hasRarity(rarity))
        .and(hasAcBetween(acMin, acMax))
        .and(hasStrMinLessThanOrEqualTo(str))
        .and(hasStealthDis(stealthDis))
        .and(hasArmorType(armorType));
        
        Page<Item> armor = itemRepository.findAll(spec, pageable);

        return armor.map(mapper::toArmorSummaryDto);
    }


}
