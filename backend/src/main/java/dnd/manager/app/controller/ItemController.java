package dnd.manager.app.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dnd.manager.app.dto.ItemDto.ItemResponseDto;
import dnd.manager.app.dto.ItemDto.ItemSummaryDto;
import dnd.manager.app.dto.ItemDto.ShieldSummaryDto;
import dnd.manager.app.dto.ItemDto.ArmorDto.ArmorSummaryDto;
import dnd.manager.app.dto.ItemDto.WeaponDto.WeaponSummaryDto;
import dnd.manager.app.service.ItemServices.ItemService;
import dnd.manager.app.service.ItemServices.ShieldService;
import dnd.manager.app.service.ItemServices.ArmorServices.ArmorService;
import dnd.manager.app.service.ItemServices.WeaponServices.WeaponService;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final ItemService itemService;
    private final WeaponService weaponService;
    private final ArmorService armorService;
    private final ShieldService shieldService;

    ItemController(ShieldService shieldService, ArmorService armorService, WeaponService weaponService, ItemService itemService) {
        this.armorService = armorService;
        this.shieldService = shieldService;
        this.weaponService = weaponService;
        this.itemService = itemService;
    }

    @GetMapping
    public ResponseEntity<Page<ItemSummaryDto>> items(
        @RequestParam(required = false) String name,
        @RequestParam(required = false) Float weightMin,
        @RequestParam(required = false) Float weightMax,
        @RequestParam(required = false) Integer priceMin,
        @RequestParam(required = false) Integer priceMax,
        @RequestParam(required = false) Boolean magic,
        @RequestParam(required = false) Boolean attunement,
        @RequestParam(required = false) List<String> rarity,
        @RequestParam(defaultValue = "0")  int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(itemService.findItems(name, weightMin, weightMax, priceMin, priceMax, magic, attunement, rarity, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemResponseDto> item(@PathVariable Long id){
        return ResponseEntity.ok(itemService.findById(id));
    }


    @GetMapping("/armor")
    public ResponseEntity<Page<ArmorSummaryDto>> armors(
        @RequestParam(required = false) String name,
        @RequestParam(required = false) Float weightMin,
        @RequestParam(required = false) Float weightMax,
        @RequestParam(required = false) Integer priceMin,
        @RequestParam(required = false) Integer priceMax,
        @RequestParam(required = false) Boolean magic,
        @RequestParam(required = false) Boolean attunement,
        @RequestParam(required = false) List<String> rarity,
        @RequestParam(required = false) Integer acMin,
        @RequestParam(required = false) Integer acMax,
        @RequestParam(required = false) Integer str,
        @RequestParam(required = false) Boolean stealthDis,
        @RequestParam(required = false) List<String> armorType,
        @RequestParam(defaultValue = "0")  int page,
        @RequestParam(defaultValue = "20") int size

    ) {
        return ResponseEntity.ok(
            armorService.findArmor(name, weightMin, weightMax, priceMin, priceMax, magic, attunement, rarity, acMin, acMax, str, stealthDis, armorType, page, size)
        );
    }

    @GetMapping("/armor/{id}")
    public ResponseEntity<ItemResponseDto> armor(@PathVariable Long id) {
        return ResponseEntity.ok(armorService.findById(id));
    }



    @GetMapping("/weapons")
    public ResponseEntity<Page<WeaponSummaryDto>> weapons(
        @RequestParam(required = false) String name,
        @RequestParam(required = false) Float weightMin,
        @RequestParam(required = false) Float weightMax,
        @RequestParam(required = false) Integer priceMin,
        @RequestParam(required = false) Integer priceMax,
        @RequestParam(required = false) Boolean magic,
        @RequestParam(required = false) Boolean attunement,
        @RequestParam(required = false) List<String> rarity,
        @RequestParam(required = false) Integer rangeMin,
        @RequestParam(required = false) Integer rangeMax,
        @RequestParam(required = false) Integer rangeNormalMin,
        @RequestParam(required = false) Integer rangeNormalMax,
        @RequestParam(required = false) Integer rangeLongMin,
        @RequestParam(required = false) Integer rangeLongMax,
        @RequestParam(required = false) String mastery,
        @RequestParam(required = false) List<String> damageTypes,
        @RequestParam(defaultValue = "0")  int page,
        @RequestParam(defaultValue = "20") int size

    ) {
        return ResponseEntity.ok(
            weaponService.findWeapons(name, weightMin, weightMax, priceMin, priceMax, magic, attunement, rarity, rangeMin, rangeMax, rangeNormalMin, rangeNormalMax, rangeLongMin, rangeLongMax, mastery, damageTypes, page, size)
        );
    }

    @GetMapping("/weapons/{id}")
    public ResponseEntity<ItemResponseDto> weapon(@PathVariable Long id) {
        return ResponseEntity.ok(weaponService.findById(id));
    }



    @GetMapping("/shields")
    public ResponseEntity<Page<ShieldSummaryDto>> shields(
        @RequestParam(required = false) String name,
        @RequestParam(required = false) Float weightMin,
        @RequestParam(required = false) Float weightMax,
        @RequestParam(required = false) Integer priceMin,
        @RequestParam(required = false) Integer priceMax,
        @RequestParam(required = false) Boolean magic,
        @RequestParam(required = false) Boolean attunement,
        @RequestParam(required = false) List<String> rarity,
        @RequestParam(required = false) Integer acBonus,
        @RequestParam(defaultValue = "0")  int page,
        @RequestParam(defaultValue = "20") int size

    ) {
        return ResponseEntity.ok(
            shieldService.findShields(name, weightMin, weightMax, priceMin, priceMax, magic, attunement, rarity, acBonus, page, size)
        );
    }

    @GetMapping("/shield/{id}")
    public ResponseEntity<ItemResponseDto> shield(@PathVariable Long id) {
        return ResponseEntity.ok(shieldService.findById(id));
    }
}
