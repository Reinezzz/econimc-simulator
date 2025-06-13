package com.example.economicssimulatorserver.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Запрос на инициацию процедуры восстановления пароля.
 *
 * @param email электронная почта пользователя
 */
public record PasswordResetRequest(
        @NotBlank String email
) {}
