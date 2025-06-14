package com.example.economicssimulatorserver.service;

import com.example.economicssimulatorserver.entity.PasswordResetToken;
import com.example.economicssimulatorserver.entity.User;
import com.example.economicssimulatorserver.repository.PasswordResetTokenRepository;
import com.example.economicssimulatorserver.repository.VerificationTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

/**
 * Сервис генерации и проверки временных кодов.
 */
@Service
@RequiredArgsConstructor
public class TokenService {

    private static final int EXP_MINUTES = 15;

    private final PasswordResetTokenRepository resetRepo;
    private final Random rng = new Random();

    /**
     * Создаёт токен для восстановления пароля пользователя.
     *
     * @param user пользователь
     * @return сгенерированный код
     */
    @Transactional
    public String createPasswordResetToken(User user) {
        resetRepo.deleteByUser(user);
        String code = randomCode();
        PasswordResetToken token = PasswordResetToken.builder()
                .user(user)
                .code(code)
                .expiresAt(LocalDateTime.now().plusMinutes(EXP_MINUTES))
                .build();
        resetRepo.save(token);
        return code;
    }

    /**
     * Проверяет код сброса пароля и удаляет его при успехе.
     *
     * @param user пользователь
     * @param code введённый код
     * @return {@code true}, если код верен
     */
    @Transactional
    public boolean validatePasswordResetCode(User user, String code) {
        return resetRepo.findByUser(user)
                .filter(t -> t.getCode().equals(code))
                .filter(t -> t.getExpiresAt().isAfter(LocalDateTime.now()))
                .map(t -> { resetRepo.delete(t); return true; })
                .orElse(false);
    }

    /**
     * Генерирует случайный шестизначный код.
     */
    private String randomCode() {
        return "%06d".formatted(rng.nextInt(1_000_000));
    }

    /**
     * Удаляет токен сброса пароля пользователя.
     */
    @Transactional
    public void evictPasswordResetToken(User user) {
        resetRepo.deleteByUser(user);
    }
}
