package com.example.economicssimulatorserver.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Запрос на подтверждение сброса пароля.
 *
 * @param email       электронная почта пользователя
 * @param code        код подтверждения из письма
 * @param newPassword новый пароль пользователя
 */
public record PasswordResetConfirm(
        @NotBlank String email,
        @NotBlank String code,
        @NotBlank String newPassword
) {}
