package com.example.economicssimulatorserver.repository;

import com.example.economicssimulatorserver.entity.PasswordResetToken;
import com.example.economicssimulatorserver.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * JPA-репозиторий для управления токенами сброса пароля пользователей.
 */
public interface PasswordResetTokenRepository
        extends JpaRepository<PasswordResetToken, Long> {

    /**
     * Поиск токена по одноразовому коду.
     */
    Optional<PasswordResetToken> findByCode(String code);

    /**
     * Поиск токена по пользователю.
     */
    Optional<PasswordResetToken> findByUser(User user);

    /**
     * Удаление токена по пользователю.
     */
    void deleteByUser(User user);
}
