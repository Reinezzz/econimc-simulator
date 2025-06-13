package com.example.economicssimulatorserver.repository;

import com.example.economicssimulatorserver.entity.ModelParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Репозиторий доступа к сущностям {@link com.example.economicssimulatorserver.entity.ModelParameter}.
 */
@Repository
public interface ModelParameterRepository extends JpaRepository<ModelParameter, Long> {

    /**
     * Возвращает параметры указанной экономической модели.
     *
     * @param modelId идентификатор модели
     * @return список параметров модели
     */
    List<ModelParameter> findByModelId(Long modelId);
}