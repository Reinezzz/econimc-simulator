package com.example.economicssimulatorserver.repository;

import com.example.economicssimulatorserver.entity.EconomicModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий для CRUD-операций над сущностями {@link com.example.economicssimulatorserver.entity.EconomicModel}.
 */

@Repository
public interface EconomicModelRepository extends JpaRepository<EconomicModel, Long> {
}