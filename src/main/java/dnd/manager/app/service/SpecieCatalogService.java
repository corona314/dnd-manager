package dnd.manager.app.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dnd.manager.app.DTO.SpecieCatalogDTO;
import dnd.manager.app.mapper.SpecieCatalogMapper;
import dnd.manager.app.repository.SpecieCatalogRepository;

@Service

public class SpecieCatalogService {

    @Autowired
    private SpecieCatalogRepository repo;
    
    public List<SpecieCatalogDTO> findAll() { 
        return repo.findAll().stream().map(SpecieCatalogMapper::toDTO).collect(Collectors.toList());
    }

    public SpecieCatalogDTO insertSpecie(SpecieCatalogDTO dto) {
        var entity = SpecieCatalogMapper.toEntity(dto);
        var savedEntity = repo.save(entity);
        return SpecieCatalogMapper.toDTO(savedEntity);
    }

}
