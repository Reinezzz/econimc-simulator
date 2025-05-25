package com.example.economicssimulatorserver.service;

import com.example.economicssimulatorserver.entity.RefreshToken;
import com.example.economicssimulatorserver.entity.User;
import com.example.economicssimulatorserver.exception.LocalizedException;
import com.example.economicssimulatorserver.repository.RefreshTokenRepository;
import com.example.economicssimulatorserver.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;

/**
 * Service for managing refresh tokens: creation, validation, and deletion.
 */
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @Value("${jwt.refresh-token-expiration-minutes:43200}") // 30 days by default
    private long refreshTokenExpirationMinutes;

    private static final SecureRandom secureRandom = new SecureRandom();

    /**
     * Создаёт новый refresh token для пользователя, основываясь на UserDetails.
     * @param userDetails детали пользователя (username — это login/email)
     * @return созданная сущность RefreshToken
     * @throws LocalizedException если пользователь не найден
     */
    @Transactional
    public RefreshToken createRefreshToken(UserDetails userDetails) {
        // Получаем username (login/email)
        String username = userDetails.getUsername();
        // Находим сущность User
        User user = userRepository.findByUsernameOrEmail(username, username)
                .orElseThrow(() -> new LocalizedException("error.user_not_found"));

        String token = generateSecureToken();
        Instant expiryDate = Instant.now().plusSeconds(refreshTokenExpirationMinutes * 60);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(token);
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(expiryDate);

        return refreshTokenRepository.save(refreshToken);
    }


    /**
     * Validates the provided refresh token.
     * @param token the refresh token string
     * @return valid RefreshToken entity
     * @throws IllegalArgumentException if token is invalid or expired
     */
    public RefreshToken validateRefreshToken(String token) {
        Optional<RefreshToken> refreshTokenOpt = refreshTokenRepository.findByToken(token);
        if (refreshTokenOpt.isEmpty()) {
            throw new IllegalArgumentException("Refresh token not found.");
        }
        RefreshToken refreshToken = refreshTokenOpt.get();
        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new IllegalArgumentException("Refresh token expired.");
        }
        return refreshToken;
    }

    /**
     * Deletes a specific refresh token.
     * @param token the refresh token string
     */
    @Transactional
    public void deleteByToken(String token) {
        refreshTokenRepository.findByToken(token)
                .ifPresent(refreshTokenRepository::delete);
    }

    /**
     * Deletes all refresh tokens belonging to a user.
     * @param user the user entity
     */
    @Transactional
    public void deleteByUser(User user) {
        refreshTokenRepository.deleteByUser(user);
    }

    /**
     * Generates a cryptographically secure random token string.
     * @return secure token
     */
    private String generateSecureToken() {
        byte[] randomBytes = new byte[64];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }
}
