package dnd.manager.app.service.ItemServices.WeaponServices;

import dnd.manager.app.model.ItemEntities.WeaponEntities.Weapon;
import dnd.manager.app.repository.ItemRepositories.WeaponRepositories.WeaponRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WeaponService {

    private final WeaponRepository weaponRepository;

    public WeaponService(WeaponRepository weaponRepository) {
        this.weaponRepository = weaponRepository;
    }

    public List<Weapon> findAll() {
        return weaponRepository.findAll();
    }

    public Optional<Weapon> findById(Long id) {
        return weaponRepository.findById(id);
    }

    public Weapon save(Weapon weapon) {
        return weaponRepository.save(weapon);
    }

    public void deleteById(Long id) {
        weaponRepository.deleteById(id);
    }
}