package com.example.economicssimulatorserver.repository;

import com.example.economicssimulatorserver.entity.UserModelParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий значений параметров модели, заданных пользователем.
 */
@Repository
public interface UserModelParameterRepository extends JpaRepository<UserModelParameter, Long> {

    /**
     * Ищет значение параметра для конкретного пользователя.
     *
     * @param userId      идентификатор пользователя
     * @param parameterId идентификатор параметра
     * @return пользовательский параметр, если найден
     */
    Optional<UserModelParameter> findByUserIdAndParameterId(Long userId, Long parameterId);

    /**
     * Возвращает все значения параметров пользователя для заданной модели.
     *
     * @param userId  идентификатор пользователя
     * @param modelId идентификатор модели
     * @return список пользовательских параметров
     */
    List<UserModelParameter> findByUserIdAndModelId(Long userId, Long modelId);
}