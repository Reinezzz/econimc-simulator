package org.example.economicssimulatorclient.dto;

/**
 * Запрос на подтверждение регистрации пользователя.
 *
 * @param email адрес электронной почты
 * @param code  код подтверждения
 */
public record VerificationRequest(
        String email,
        String code
) {}
