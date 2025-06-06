package com.example.economicssimulatorserver.repository;

import com.example.economicssimulatorserver.entity.DocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentRepository extends JpaRepository<DocumentEntity, Long> {
    List<DocumentEntity> findByUserId(Long userId);
}
