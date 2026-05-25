package dnd.manager.app.service.ItemServices.WeaponServices;

import dnd.manager.app.model.ItemEntities.WeaponEntities.WeaponProperty;
import dnd.manager.app.repository.ItemRepositories.WeaponRepositories.WeaponPropertyRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WeaponPropertyService {

    private final WeaponPropertyRepository weaponPropertyRepository;

    public WeaponPropertyService(WeaponPropertyRepository weaponPropertyRepository) {
        this.weaponPropertyRepository = weaponPropertyRepository;
    }

    public List<WeaponProperty> findAll() {
        return weaponPropertyRepository.findAll();
    }

    public Optional<WeaponProperty> findById(Long id) {
        return weaponPropertyRepository.findById(id);
    }

    public WeaponProperty save(WeaponProperty weaponProperty) {
        return weaponPropertyRepository.save(weaponProperty);
    }

    public void deleteById(Long id) {
        weaponPropertyRepository.deleteById(id);
    }
}