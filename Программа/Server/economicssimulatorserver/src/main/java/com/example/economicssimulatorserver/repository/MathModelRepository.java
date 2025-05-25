package com.example.economicssimulatorserver.repository;

import com.example.economicssimulatorserver.entity.MathModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Репозиторий для работы с математическими моделями.
 */
@Repository
public interface MathModelRepository extends JpaRepository<MathModel, Long> {

    /**
     * Находит все математические модели пользователя по userId.
     *
     * @param userId идентификатор пользователя
     * @return список моделей пользователя
     */
    List<MathModel> findByUserId(Long userId);
}
