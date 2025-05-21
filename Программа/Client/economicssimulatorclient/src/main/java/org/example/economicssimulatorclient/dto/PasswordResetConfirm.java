package org.example.economicssimulatorclient.dto;

/**
 * DTO для подтверждения сброса пароля.
 *
 * @param email email пользователя
 * @param code одноразовый код для сброса пароля
 * @param newPassword новый пароль пользователя
 */
public record PasswordResetConfirm(
        String email,
        String code,
        String newPassword
) {}
