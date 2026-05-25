package dnd.manager.app.service.ItemServices.WeaponServices;

import java.util.List;

import org.springframework.stereotype.Service;

import dnd.manager.app.model.ItemEntities.WeaponEntities.Mastery;
import dnd.manager.app.repository.ItemRepositories.WeaponRepositories.MasteryRepository;

@Service
class MasteryService {

    private final MasteryRepository masteryRepository;

    MasteryService(MasteryRepository masteryRepository) {
        this.masteryRepository = masteryRepository;
    }

    public List<Mastery> findAll() {
        return masteryRepository.findAll();
    }

    public Mastery findById(Long id) {
        return masteryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mastery not found with id: " + id));
    }

    public Mastery save(Mastery mastery) {
        return masteryRepository.save(mastery);
    }
}