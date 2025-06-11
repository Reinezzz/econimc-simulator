package com.example.economicssimulatorserver.repository;

import com.example.economicssimulatorserver.entity.PasswordResetToken;
import com.example.economicssimulatorserver.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository
        extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByCode(String code);

    Optional<PasswordResetToken> findByUser(User user);

    void deleteByUser(User user);
}
