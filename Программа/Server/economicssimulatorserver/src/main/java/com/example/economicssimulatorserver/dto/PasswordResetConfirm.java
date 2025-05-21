package com.example.economicssimulatorserver.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Запрос для подтверждения сброса пароля по email и коду.
 *
 * @param email       email пользователя
 * @param code        одноразовый код для сброса пароля
 * @param newPassword новый пароль пользователя
 */
public record PasswordResetConfirm(
        @NotBlank String email,
        @NotBlank String code,
        @NotBlank String newPassword
) {}
