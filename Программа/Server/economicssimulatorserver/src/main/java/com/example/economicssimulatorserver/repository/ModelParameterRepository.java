package com.example.economicssimulatorserver.repository;

import com.example.economicssimulatorserver.entity.ModelParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Репозиторий для работы с параметрами математических моделей.
 */
@Repository
public interface ModelParameterRepository extends JpaRepository<ModelParameter, Long> {

    /**
     * Находит все параметры по идентификатору модели.
     *
     * @param mathModelId идентификатор модели
     * @return список параметров для модели
     */
    List<ModelParameter> findByMathModelId(Long mathModelId);
}
