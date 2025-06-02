package com.example.economicssimulatorserver.repository;

import com.example.economicssimulatorserver.entity.ModelParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModelParameterRepository extends JpaRepository<ModelParameter, Long> {
    List<ModelParameter> findByModelId(Long modelId);
}
