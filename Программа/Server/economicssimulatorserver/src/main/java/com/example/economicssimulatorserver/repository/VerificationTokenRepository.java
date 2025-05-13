package com.example.economicssimulatorserver.repository;

import com.example.economicssimulatorserver.entity.VerificationToken;
import com.example.economicssimulatorserver.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VerificationTokenRepository
        extends JpaRepository<VerificationToken, Long> {

    Optional<VerificationToken> findByCode(String code);
    Optional<VerificationToken> findByUser(User user);
    void deleteByUser(User user);
}
