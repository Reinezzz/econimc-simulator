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

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @Value("${jwt.refresh-token-expiration-minutes:43200}") // 30 days by default
    private long refreshTokenExpirationMinutes;

    private static final SecureRandom secureRandom = new SecureRandom();

    @Transactional
    public RefreshToken createRefreshToken(UserDetails userDetails) {
        String username = userDetails.getUsername();
        User user = userRepository.findByUsernameOrEmail(username, username)
                .orElseThrow(() -> new LocalizedException("error.user_not_found"));

        Optional<RefreshToken> existingToken = refreshTokenRepository.findByUser(user)
                .filter(rt -> rt.getExpiryDate().isAfter(Instant.now()));
        if (existingToken.isPresent()) {
            return existingToken.get();
        }
        refreshTokenRepository.deleteByUser(user);

        String token = generateSecureToken();
        Instant expiryDate = Instant.now().plusSeconds(refreshTokenExpirationMinutes * 60);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(token);
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(expiryDate);

        return refreshTokenRepository.save(refreshToken);
    }


    public RefreshToken validateRefreshToken(String token) {
        Optional<RefreshToken> refreshTokenOpt = refreshTokenRepository.findByToken(token);
        if (refreshTokenOpt.isEmpty()) {
            throw new LocalizedException("error.refresh_token_not_found");
        }
        RefreshToken refreshToken = refreshTokenOpt.get();
        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new LocalizedException("error.refresh_token_expired");
        }
        return refreshToken;
    }

    @Transactional
    public void deleteByToken(String token) {
        System.out.println("Token for delete: " + token);
        refreshTokenRepository.findByToken(token)
                .ifPresentOrElse(
                        t -> System.out.println("found, deleting..."),
                        () -> System.out.println("No token!")
                );

        refreshTokenRepository.findByToken(token)
                .ifPresent(refreshTokenRepository::delete);
    }

    @Transactional
    public void deleteByUser(User user) {
        refreshTokenRepository.deleteByUser(user);
    }

    private String generateSecureToken() {
        byte[] randomBytes = new byte[64];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }
}
