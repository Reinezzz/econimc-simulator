package com.example.economicssimulatorserver.repository;

import com.example.economicssimulatorserver.entity.ModelResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для сохранения и получения сущностей {@link com.example.economicssimulatorserver.entity.ModelResult}.
 */
@Repository
public interface ModelResultRepository extends JpaRepository<ModelResult, Long> {

    /**
     * Получает результат пользователя по конкретной модели.
     *
     * @param userId  идентификатор пользователя
     * @param modelId идентификатор модели
     * @return результат модели, если найден
     */
    Optional<ModelResult> findByUserIdAndModelId(Long userId, Long modelId);

}