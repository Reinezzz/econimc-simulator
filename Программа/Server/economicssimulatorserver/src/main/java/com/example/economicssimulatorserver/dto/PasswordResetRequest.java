package com.example.economicssimulatorserver.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Запрос на инициацию процедуры сброса пароля по email.
 *
 * @param email email пользователя
 */
public record PasswordResetRequest(
        @NotBlank String email
) {}
