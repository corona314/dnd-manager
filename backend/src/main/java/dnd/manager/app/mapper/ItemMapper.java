package dnd.manager.app.mapper;

import org.springframework.stereotype.Component;

import dnd.manager.app.dto.ItemDto.ItemFeatureDto;
import dnd.manager.app.dto.ItemDto.ItemResponseDto;
import dnd.manager.app.dto.ItemDto.ItemSummaryDto;
import dnd.manager.app.dto.ItemDto.ShieldResponseDto;
import dnd.manager.app.dto.ItemDto.ShieldSummaryDto;
import dnd.manager.app.dto.ItemDto.ArmorDto.ArmorResponseDto;
import dnd.manager.app.dto.ItemDto.ArmorDto.ArmorSummaryDto;
import dnd.manager.app.dto.ItemDto.WeaponDto.MasteryDto;
import dnd.manager.app.dto.ItemDto.WeaponDto.WeaponDamageDto;
import dnd.manager.app.dto.ItemDto.WeaponDto.WeaponResponseDto;
import dnd.manager.app.dto.ItemDto.WeaponDto.WeaponSummaryDto;
import dnd.manager.app.dto.ItemDto.WeaponDto.WeaponWeaponPropertyDto;
import dnd.manager.app.model.FeatureEntities.Feature;
import dnd.manager.app.model.ItemEntities.Item;
import dnd.manager.app.model.ItemEntities.ArmorEntities.Armor;
import dnd.manager.app.model.ItemEntities.ShieldEntities.Shield;
import dnd.manager.app.model.ItemEntities.WeaponEntities.Mastery;
import dnd.manager.app.model.ItemEntities.WeaponEntities.Weapon;
import dnd.manager.app.model.ItemEntities.WeaponEntities.WeaponDamage;
import dnd.manager.app.model.ItemEntities.WeaponEntities.WeaponWeaponProperty;

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
            i.getRarity(),
            i.getDescription(),
            i.getFeatures().stream().map(this::toFeatureDto).toList(),
            i.getArmor() == null ? null : toArmorResponseDto(i.getArmor()),
            i.getWeapon() == null ? null : toWeaponResponseDto(i.getWeapon()),
            i.getShield() == null ? null : toShieldResponseDto(i.getShield())
        );
    }

    public ItemSummaryDto toSummaryDto(Item i){
        return new ItemSummaryDto(
            i.getId(),
            i.getName(),
            i.getWeight(),
            i.getPrice(),
            i.getItemType() == null ? null : i.getItemType().getName(),
            i.getMagic(),
            i.getAttunement(),
            i.getRarity()
        );
    }

    public ItemFeatureDto toFeatureDto(Feature f){
        return new ItemFeatureDto(
            f.getName(),
            f.getDescription()
        );
    }

    private ArmorResponseDto toArmorResponseDto(Armor a) {
        return new ArmorResponseDto(
            a.getAcBase(),
            a.getAcMax(),
            a.getStrMin(),
            a.getStealthDis(),
            a.getArmorType() == null ? null : a.getArmorType().getName()
        );
    }

    private WeaponResponseDto toWeaponResponseDto(Weapon w) {
        return new WeaponResponseDto(
            w.getRangeNormal(),
            w.getRangeLong(),
            w.getWeaponCategory() == null ? null : w.getWeaponCategory().toString(),
            w.getWeaponType() == null ? null : w.getWeaponType().toString(),
            w.getMastery() == null ? null : toMasteryDto(w.getMastery()),
            w.getWeaponDamages() == null ? null : w.getWeaponDamages().stream().map(this::toWeaponDamageDto).toList(),
            w.getProperties() == null ? null : w.getProperties().stream().map(this::toWeaponPropertyDto).toList()
        );
    }

    private MasteryDto toMasteryDto(Mastery m) {
        return new MasteryDto(m.getName(), m.getDescription());
    }

    private WeaponDamageDto toWeaponDamageDto(WeaponDamage wd) {
        return new WeaponDamageDto(
            wd.getDamageRoll(), 
            wd.getDamageType() == null ? null : wd.getDamageType().getName(), 
            wd.getAlways()
        );
    }

    private WeaponWeaponPropertyDto toWeaponPropertyDto(WeaponWeaponProperty wp){
        return new WeaponWeaponPropertyDto(
            wp.getProperty() == null ? null : wp.getProperty().getName(),
            wp.getProperty() == null ? null : wp.getProperty().getDescription(),
            wp.getValue()
        );
    }

    private ShieldResponseDto toShieldResponseDto(Shield s) {
        return new ShieldResponseDto(s.getAcBonus());

    }

    public ArmorSummaryDto toArmorSummaryDto(Item i){
        return new ArmorSummaryDto(
            i.getId(),
            i.getName(),
            i.getWeight(),
            i.getPrice(),
            i.getItemType() == null ? null : i.getItemType().getName(),
            i.getMagic(),
            i.getAttunement(),
            i.getRarity(),
            i.getArmor() == null ? null : i.getArmor().getAcBase(),
            i.getArmor() == null ? null : i.getArmor().getAcMax(),
            i.getArmor() == null ? null : i.getArmor().getStrMin(),
            i.getArmor() == null ? null : i.getArmor().getStealthDis(),
            i.getArmor() == null ? null : i.getArmor().getArmorType() == null ? null : i.getArmor().getArmorType().getName()

        );
    }

    public WeaponSummaryDto toWeaponSummaryDto(Item i){
        return new WeaponSummaryDto(
            i.getId(),
            i.getName(),
            i.getWeight(),
            i.getPrice(),
            i.getItemType() == null ? null : i.getItemType().getName(),
            i.getMagic(),
            i.getAttunement(),
            i.getRarity(),
            i.getWeapon() == null ? null : i.getWeapon().getRangeNormal(),
            i.getWeapon() == null ? null : i.getWeapon().getRangeLong(),
            i.getWeapon() == null ? null : i.getWeapon().getMastery() == null ? null : i.getWeapon().getMastery().getName(),
            i.getWeapon() == null ? null : i.getWeapon().getWeaponCategory() == null ? null : i.getWeapon().getWeaponCategory().name(),
            i.getWeapon() == null ? null : i.getWeapon().getWeaponDamages() == null ? null : i.getWeapon().getWeaponDamages().stream().map(wd -> wd.getDamageType().getName()).toList()
        );
    }

    public ShieldSummaryDto toShieldSummaryDto(Item i){
        return new ShieldSummaryDto(
            i.getId(),
            i.getName(),
            i.getWeight(),
            i.getPrice(),
            i.getItemType() == null ? null : i.getItemType().getName(),
            i.getMagic(),
            i.getAttunement(),
            i.getRarity(),
            i.getShield() == null ? null : i.getShield().getAcBonus()
        );
    }


}
