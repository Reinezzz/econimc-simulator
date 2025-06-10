package com.example.economicssimulatorserver.repository;

import com.example.economicssimulatorserver.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    List<Report> findByUserId(Long userId);

    List<Report> findByUserIdOrderByCreatedAtDesc(Long userId);

    // При необходимости добавить другие методы, например:
    // Optional<Report> findByIdAndUserId(Long id, Long userId);
}
