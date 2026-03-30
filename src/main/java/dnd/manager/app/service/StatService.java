package dnd.manager.app.service;

import dnd.manager.app.model.Stat;
import dnd.manager.app.repository.StatRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StatService {

    private final StatRepository statRepository;

    public StatService(StatRepository statRepository) {
        this.statRepository = statRepository;
    }

    public List<Stat> findAll() {
        return statRepository.findAll();
    }

    public Optional<Stat> findById(Long id) {
        return statRepository.findById(id);
    }

    public Optional<Stat> findByCode(String code) {
        return statRepository.findByCode(code);
    }

    public Stat save(Stat stat) {
        return statRepository.save(stat);
    }

    public void deleteById(Long id) {
        statRepository.deleteById(id);
    }
}