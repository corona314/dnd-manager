package dnd.manager.app.service;

import java.util.List;

import org.springframework.stereotype.Service;
import dnd.manager.app.model.Mastery;
import dnd.manager.app.repository.MasteryRepository;

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