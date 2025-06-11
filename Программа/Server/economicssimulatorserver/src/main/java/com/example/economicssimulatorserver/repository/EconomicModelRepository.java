package com.example.economicssimulatorserver.repository;

import com.example.economicssimulatorserver.entity.EconomicModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EconomicModelRepository extends JpaRepository<EconomicModel, Long> {
}
