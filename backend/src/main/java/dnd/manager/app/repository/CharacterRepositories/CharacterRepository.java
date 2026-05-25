package dnd.manager.app.repository.CharacterRepositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import dnd.manager.app.model.CharacterEntities.CharacterEntity;

public interface CharacterRepository extends JpaRepository<CharacterEntity, Long> {

    List<CharacterEntity> findByUserId(Long userId);

    CharacterEntity findByUserIdAndId(Long userId, Long id);
}
