package dnd.manager.app.service;

import java.util.List;

import org.springframework.stereotype.Service;

import dnd.manager.app.model.Stat;
import dnd.manager.app.repository.StatRepository;

@Service
public class StatService {

    private final StatRepository statRepository;

    public StatService(StatRepository statRepository) {
        this.statRepository = statRepository;
    }

    public List<Stat> findAll() {
        return statRepository.findAll();
    }

    public Stat findById(Long id) {
        return statRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stat not found with id: " + id));
    }

    public Stat findByCode(String code) {
        return statRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Stat not found with code: " + code));
    }

    public int calculateModifier(int baseValue) {
        return (int) Math.floor((baseValue - 10) / 2.0);
    }
}