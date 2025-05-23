package com.example.economicssimulatorserver.service;

import com.example.economicssimulatorserver.entity.RefreshToken;
import com.example.economicssimulatorserver.entity.User;
import com.example.economicssimulatorserver.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;

/**
 * Сервис для генерации, валидации и ротации refresh-токенов.
 */
@Service
public class RefreshTokenService {
    private final RefreshTokenRepository repository;
    private final long refreshTokenDurationMs;

    public RefreshTokenService(
            RefreshTokenRepository repository,
            @Value("${jwt.refreshTokenDurationMs:604800000}") long refreshTokenDurationMs // 7 дней по умолчанию
    ) {
        this.repository = repository;
        this.refreshTokenDurationMs = refreshTokenDurationMs;
    }

    /**
     * Генерирует и сохраняет новый refresh-токен для пользователя.
     */
    @Transactional
    public RefreshToken createRefreshToken(UserDetails userDetails) {
        String token = generateSecureToken();
        Instant expiryDate = Instant.now().plusMillis(refreshTokenDurationMs);
        RefreshToken refreshToken = new RefreshToken(user, token, expiryDate);
        return repository.save(refreshToken);
    }

    /**
     * Проверяет токен, выбрасывает исключение если истёк.
     */
    public RefreshToken validateRefreshToken(String token) {
        RefreshToken refreshToken = repository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));
        if (refreshToken.isExpired()) {
            repository.delete(refreshToken);
            throw new RuntimeException("Refresh token expired");
        }
        return refreshToken;
    }

    /**
     * Ротирует (заменяет) refresh-токен на новый.
     */
    @Transactional
    public RefreshToken rotateRefreshToken(RefreshToken oldToken) {
        repository.delete(oldToken);
        return createRefreshToken(oldToken.getUser());
    }

    /**
     * Удаляет все refresh-токены пользователя (logout).
     */
    @Transactional
    public void deleteByUser(User user) {
        repository.deleteAllByUser(user);
    }

    // Генерация безопасного случайного токена
    private String generateSecureToken() {
        byte[] randomBytes = new byte[64];
        new SecureRandom().nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }
}
