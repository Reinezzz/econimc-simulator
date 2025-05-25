package com.example.economicssimulatorserver.repository;

import com.example.economicssimulatorserver.entity.ComputationResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Репозиторий для работы с результатами вычислений.
 */
@Repository
public interface ComputationResultRepository extends JpaRepository<ComputationResult, Long> {

    /**
     * Находит все результаты для конкретной математической модели.
     *
     * @param mathModelId идентификатор математической модели
     * @return список результатов вычислений
     */
    List<ComputationResult> findByMathModelId(Long mathModelId);
}
