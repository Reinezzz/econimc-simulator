package org.example.economicssimulatorclient.dto;

/**
 * Подтверждение сброса пароля пользователем.
 *
 * @param email       адрес электронной почты
 * @param code        код подтверждения
 * @param newPassword новый пароль
 */
public record PasswordResetConfirm(
        String email,
        String code,
        String newPassword
) {}
