package com.example.economicssimulatorserver.repository;

import com.example.economicssimulatorserver.entity.UserModelParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserModelParameterRepository extends JpaRepository<UserModelParameter, Long> {

    Optional<UserModelParameter> findByUserIdAndParameterId(Long userId, Long parameterId);

    List<UserModelParameter> findByUserIdAndModelId(Long userId, Long modelId);
}
