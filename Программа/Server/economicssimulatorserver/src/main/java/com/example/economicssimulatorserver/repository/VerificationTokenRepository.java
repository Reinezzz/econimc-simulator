package com.example.economicssimulatorserver.repository;

import com.example.economicssimulatorserver.entity.VerificationToken;
import com.example.economicssimulatorserver.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * JPA-репозиторий для управления токенами подтверждения email.
 */
public interface VerificationTokenRepository
        extends JpaRepository<VerificationToken, Long> {

    /**
     * Поиск токена по одноразовому коду.
     */
    Optional<VerificationToken> findByCode(String code);

    /**
     * Поиск токена по пользователю.
     */
    Optional<VerificationToken> findByUser(User user);

    /**
     * Удаление токена по пользователю.
     */
    void deleteByUser(User user);
}
