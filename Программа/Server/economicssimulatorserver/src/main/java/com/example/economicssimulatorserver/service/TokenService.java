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
 * Сервис для управления одноразовыми токенами (сброс пароля, верификация email).
 */
@Service
@RequiredArgsConstructor
public class TokenService {

    private static final int CODE_LENGTH = 6;
    private static final int EXP_MINUTES = 15;

    private final VerificationTokenRepository verifyRepo;
    private final PasswordResetTokenRepository resetRepo;
    private final Random rng = new Random();

    /**
     * Создает и сохраняет токен для сброса пароля пользователя, отправляет код на email.
     * Старые токены удаляются.
     * @param user пользователь
     * @return одноразовый код для сброса пароля
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
     * Проверяет валидность кода для сброса пароля и удаляет токен при успехе.
     * @param user пользователь
     * @param code проверяемый одноразовый код
     * @return true, если код верный и не истек
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
     * Генерирует случайный 6-значный цифровой код.
     * @return строка из 6 цифр
     */
    private String randomCode() {
        return "%06d".formatted(rng.nextInt(1_000_000));
    }

    /**
     * Удаляет все токены сброса пароля для пользователя.
     * @param user пользователь
     */
    @Transactional
    public void evictPasswordResetToken(User user) {
        resetRepo.deleteByUser(user);
    }
}
