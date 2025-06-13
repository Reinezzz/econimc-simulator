package com.example.economicssimulatorserver.repository;

import com.example.economicssimulatorserver.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Репозиторий для доступа к сущностям {@link com.example.economicssimulatorserver.entity.Report}.
 */
@Repository

public interface ReportRepository extends JpaRepository<Report, Long> {

    /**
     * Возвращает отчёты, созданные конкретным пользователем.
     *
     * @param userId идентификатор пользователя
     * @return список отчётов
     */
    List<Report> findByUserId(Long userId);
}