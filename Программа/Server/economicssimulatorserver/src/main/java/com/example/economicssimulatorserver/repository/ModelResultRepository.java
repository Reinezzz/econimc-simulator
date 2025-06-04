package com.example.economicssimulatorserver.repository;

import com.example.economicssimulatorserver.entity.ModelResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ModelResultRepository extends JpaRepository<ModelResult, Long> {
    List<ModelResult> findByModelId(Long modelId);
    Optional<ModelResult> findByUserIdAndModelId(Long userId, Long modelId);

}
